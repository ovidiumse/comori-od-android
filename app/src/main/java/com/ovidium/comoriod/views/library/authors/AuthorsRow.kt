package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem
import com.ovidium.comoriod.views.ItemCategory

@Composable
fun AuthorsRow(
    navController: NavController,
    response: AuthorsResponse?,
    isLoading: Boolean,
    isDark: Boolean,
    showAuthorAction: (Bucket?) -> Unit
) {
    val items = response?.aggregations?.authors?.buckets?.map { it }

    AuthorsCarousel("Autori", items, 2, isLoading, showAuthorAction)
}