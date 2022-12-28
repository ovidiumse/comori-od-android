package com.ovidium.comoriod.data.recentlyaddedbooks

import com.google.gson.annotations.SerializedName

data class Author(
    val name: String,
    @SerializedName("photo-url-lg")
    val photo_url_lg: String?,
    @SerializedName("photo-url-sm")
    val photo_url_sm: String?
)