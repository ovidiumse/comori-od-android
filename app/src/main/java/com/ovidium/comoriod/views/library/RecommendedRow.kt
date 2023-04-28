package com.ovidium.comoriod.views.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ovidium.comoriod.api.RetrofitBuilder
import com.ovidium.comoriod.data.recommended.RecommendedResponseItem
import com.ovidium.comoriod.utils.Resource
import com.ovidium.comoriod.utils.Status
import com.ovidium.comoriod.utils.getVolumeCoverGradient

@Composable
fun RecommendedRow(
    navController: NavController,
    response: MutableState<Resource<SnapshotStateList<RecommendedResponseItem>>>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    when (response.value.status) {
        Status.SUCCESS -> {
            val items = response.value.data?.map { item ->
                DataItem(
                    item.title,
                    item._id,
                    item.author,
                    item.book,
                    item.author_photo_url_sm ?: (RetrofitBuilder.BASE_URL + "img/ic_unknown_person_sm.jpg"),
                    getVolumeCoverGradient(item.volume, isDark),
                    type = ItemCategory.Article
                )
            }

            if (!items.isNullOrEmpty())
                HorizontalCarousel(navController, "Recomandate", items, 5, false, modifier = modifier)
        }

        Status.ERROR -> {}
        Status.LOADING -> HorizontalCarousel(navController, "Recomandate", listOf(), 5, true, modifier = modifier)
        else -> {}
    }
}