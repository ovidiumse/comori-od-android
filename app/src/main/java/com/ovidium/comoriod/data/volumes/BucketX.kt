package com.ovidium.comoriod.data.volumes

import com.google.gson.annotations.SerializedName

data class BucketX(
    val doc_count: Int,
    val key: String,
    @SerializedName("photo-url-lg")
    val photo_url_lg: String?,
    @SerializedName("photo-url-sm")
    val photo_url_sm: String?
)