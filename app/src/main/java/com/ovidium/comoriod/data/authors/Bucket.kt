package com.ovidium.comoriod.data.authors

import com.google.gson.annotations.SerializedName

data class Bucket(
    val books: Books,
    val dob: String?,
    val doc_count: Int,
    val dod: String?,
    val key: String,
    val min_date_added: MinDateAdded,
    val min_insert_ts: MinInsertTs,
    val name: String,
    @SerializedName("short-about")
    val shortAbout: String?,
    @SerializedName("photo-url-sm")
    val photo_url_sm: String?,
    @SerializedName("photo-url-lg")
    val photo_url_lg: String?,
    val types: Types,
    val volumes: Volumes
)