package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.mappings.getDrawableByAuthor
import com.ovidium.comoriod.utils.getVolumeCoverGradient
import com.ovidium.comoriod.views.DataItem
import com.ovidium.comoriod.views.ItemCategory

@Composable
fun AuthorsRow(navController: NavController, response: AuthorsResponse?, isLoading: Boolean, isDark: Boolean) {
    val items = response?.aggregations?.authors?.buckets?.map { bucket ->
        DataItem(
            bucket.key,
            "",
            "",
            getDrawableByAuthor(bucket.key),
            getVolumeCoverGradient("", isDark),
            type = ItemCategory.Author
        )
    }

    AuthorsCarousel("Autori", items, 2, isLoading)
}