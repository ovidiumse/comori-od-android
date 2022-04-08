package com.ovidium.comoriod.mappings

import com.ovidium.comoriod.R

fun getDrawableByAuthor(author: String): Int {
    return when(author) {
        "Traian Dorz" -> R.drawable.ic_traian_dorz
        "Pr. Iosif Trifa" -> R.drawable.ic_pr_iosif_trifa
        else -> R.drawable.ic_unknown_person
    }
}