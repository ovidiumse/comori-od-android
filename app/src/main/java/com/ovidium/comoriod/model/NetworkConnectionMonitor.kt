package com.ovidium.comoriod.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}

val currentConnectivityState: ConnectionState
    get() {
        return ConnectionState.Available
    }

    fun Context.observeConnectivityAsFlow() = callbackFlow {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkStatusCallback = object: ConnectivityManager.NetworkCallback() {
            override fun onUnavailable() {
                println("on-Unavailable")
                trySend(ConnectionState.Unavailable).isSuccess
            }

            override fun onAvailable(network: Network) {
                println("on-Available")
                trySend(ConnectionState.Available).isSuccess
            }

            override fun onLost(network: Network) {
                println("on-Lost")
                trySend(ConnectionState.Unavailable).isSuccess
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStatusCallback)
        }
    }

@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current

    return produceState(initialValue = currentConnectivityState) {
        context.observeConnectivityAsFlow().collect { value = it }
    }
}