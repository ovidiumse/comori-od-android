package com.ovidium.comoriod.google

import android.net.Uri

data class GoogleUserModel(
    val id: String,
    val displayName: String,
    val email: String,
    val photoUrl: Uri,
    val issuer: String
) {

}
