package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.data.authors.Bucket

@Composable
fun AuthorsRow(
    navController: NavController,
    response: AuthorsResponse?,
    isLoading: Boolean,
    isDark: Boolean,
    showAuthorAction: (Bucket?) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = response?.aggregations?.authors?.buckets?.map { it }

    AuthorsCarousel("Autori", items, 2, isLoading, showAuthorAction, modifier)
}