package com.ovidium.comoriod.model

import android.app.Application
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.Keep
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.ovidium.comoriod.google.GoogleApiContract
import com.ovidium.comoriod.google.GoogleUserModel
import com.ovidium.comoriod.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

enum class UserState {
    Unknown,
    NotLoggedIn,
    Loading,
    LoggedIn,
    Error
}

data class UserResource(val user: GoogleUserModel?, val state: UserState, val message: String?) {
    companion object {
        fun unknown(): UserResource = UserResource(null, UserState.Unknown, null)
        fun notLoggedIn(): UserResource = UserResource(null, UserState.NotLoggedIn, null)
        fun loading(): UserResource = UserResource(null, UserState.Loading, null)
        fun loggedIn(user: GoogleUserModel): UserResource =
            UserResource(user, UserState.LoggedIn, null)

        fun error(message: String): UserResource = UserResource(null, UserState.Error, message)
    }
}

@Keep
class GoogleSignInModel(context: Context) : ViewModel() {
    private val errorMessage = "Ceva nu a mers bine."

    private var userResourceState = mutableStateOf(UserResource.unknown())
    val userResource = userResourceState

    fun retrieveUser(task: Task<GoogleSignInAccount>?) {
        viewModelScope.launch {
            try {
                val account = task?.getResult(ApiException::class.java)
                if (account != null)
                    userResourceState.value =
                        UserResource.loggedIn(
                            GoogleUserModel(
                                account.id,
                                account.displayName,
                                account.email,
                                account.photoUrl,
                                "google.com"
                            )
                        )
                else
                    userResourceState.value = UserResource.error(errorMessage)
            } catch (e: ApiException) {
                userResourceState.value = UserResource.error(e.message ?: errorMessage)
            }
        }
    }

    fun silentSignIn(context: Context) {
        viewModelScope.launch {
            userResourceState.value = UserResource.loading()
            try {
                when (val account = GoogleSignIn.getLastSignedInAccount(context)) {
                    null -> userResourceState.value = UserResource.notLoggedIn()
                    else -> userResourceState.value = UserResource.loggedIn(
                        GoogleUserModel(
                            account.id,
                            account.displayName,
                            account.email,
                            account.photoUrl,
                            "google.com"
                        )
                    )
                }
            } catch (e: Exception) {
                userResourceState.value = UserResource.error(e.message ?: errorMessage)
            }
        }
    }

    fun signIn(
        launcher: ManagedActivityResultLauncher<Int, Task<GoogleSignInAccount>?>,
        requestId: Int
    ) {
        viewModelScope.launch {
            userResourceState.value = UserResource.loading()
            try {
                launcher.launch(requestId)
            } catch (e: Exception) {
                userResourceState.value = UserResource.error(e.message ?: errorMessage)
            }
        }
    }

    fun signOut(applicationContext: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestId()
            .requestEmail()
            .requestProfile()
            .build()

        val client = GoogleSignIn.getClient(applicationContext, gso)
        val task = client.signOut()
        task.addOnCompleteListener { userResourceState.value = UserResource.notLoggedIn() }
    }
}

class GoogleSignInModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Context::class.java).newInstance(context)
    }
}