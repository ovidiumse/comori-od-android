package com.ovidium.comoriod.utils

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun nowUtc(): ZonedDateTime? {
    return ZonedDateTime.now(ZoneOffset.UTC)
}

fun toIsoString(dateTime: ZonedDateTime?): String? {
    return dateTime?.format(DateTimeFormatter.ISO_DATE_TIME)
}
