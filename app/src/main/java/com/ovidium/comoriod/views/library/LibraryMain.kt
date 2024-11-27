package com.ovidium.comoriod.views.library

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ovidium.comoriod.data.authors.AuthorsResponse
import com.ovidium.comoriod.data.authors.Bucket
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.UserState
import com.ovidium.comoriod.ui.theme.Montserrat
import com.ovidium.comoriod.ui.theme.colors
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Status

@Composable
fun LibraryMain(
    navController: NavController,
    signInModel: GoogleSignInModel,
    libraryModel: LibraryModel,
    isDark: Boolean,
    showAuthorAction: (Bucket?) -> Unit
) {
    if (libraryModel.recommendedData.value.status == Status.UNINITIALIZED && signInModel.userResource.value.state == UserState.LoggedIn)
        libraryModel.loadRecommended()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = getNamedColor("Background", isDark))
    ) {
        item {
            StateHandler(navController, libraryModel.authorsData) { data, isLoading ->
                BibleBanner(data)
            }
        }
        item {
            StateHandler(
                navController,
                libraryModel.recentlyAddedBooksData
            ) { data, isLoading ->
                RecentlyAddedBooksRow(
                    navController, data, isLoading, isDark, modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 300)
                    )
                )
            }
        }

        if (signInModel.userResource.value.state == UserState.LoggedIn) {
            item {
                RecommendedRow(
                    navController, libraryModel.recommendedData, isDark, modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 300)
                    )
                )
            }
            item {
                StateHandler(navController, libraryModel.trendingData) { data, isLoading ->
                    TrendingRow(
                        navController, data, isLoading, isDark, modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(durationMillis = 300)
                        )
                    )
                }
            }
        }
        item {
            StateHandler(navController, libraryModel.authorsData) { data, isLoading ->
                AuthorsRow(
                    navController, data, isLoading, isDark, showAuthorAction, modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 300)
                    )
                )
            }
        }
        item {
            StateHandler(navController, libraryModel.volumesData) { data, isLoading ->
                VolumesRow(
                    navController, data, isLoading, isDark, modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(durationMillis = 300)
                    )
                )
            }
        }
    }
}

@Composable
private fun BibleBanner(data: AuthorsResponse?) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(12.dp))
    ) {
        BibleBannerBackground()
        Column(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Cercetează",
                fontSize = 18.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Biblia",
                modifier = Modifier.offset(y = -8.dp),
                fontSize = 36.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(
                modifier = Modifier
                    .offset(y = -8.dp)
            ) {
                val dataItems = data?.aggregations?.authors?.buckets?.map { it }
                dataItems?.let {
                    itemsIndexed(dataItems.subList(fromIndex = 0, toIndex = 6)) { index, item ->
                        Image(
                            painter = rememberAsyncImagePainter(item.photo_url_sm),
                            contentDescription = "bible authors",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .requiredSize(35.dp)
                                .clip(RoundedCornerShape(100))
                        )
                    }
                }
            }
            Text(
                text = "sub îndrumarea înaintașilor",
//                modifier = Modifier.offset(y = -20.dp),
                fontSize = 16.sp,
                fontFamily = Montserrat
            )
        }

    }
}


@Composable
fun BibleBannerBackground() {
    val bgText = """
Binecuvântat să fie Dumnezeu, Tatăl Domnului nostru Isus Hristos, care, după îndurarea Sa cea mare, ne-a† născut din nou, prin†† învierea lui Isus Hristos din morţi, la o nădejde vie 2 Cor 1:3 Efes 1:3 ** Tit 3:5 † Ioan 3:3 Ioan 3:5 Iac 1:18 †† 1 Cor 15:20 1 Tes 4:14 1 Pet 3:21 4 şi la o moştenire nestricăcioasă şi neîntinată şi care nu se poate veşteji, păstrată în ceruri pentru voi. El a fost cunoscut mai înainte de întemeierea lumii şi a fost arătat la sfârşitul vremurilor pentru voi care, prin El, sunteţi credincioşi în Dumnezeu, care L-a înviat din morţi şi I-a dat slavă, pentru ca credinţa şi nădejdea voastră să fie în Dumnezeu.
"""
    val gradient = Brush.verticalGradient(
        0.0f to getNamedColor(name = "Coral", isDark = isSystemInDarkTheme()),
        0.3f to getNamedColor(name = "Skye", isDark = isSystemInDarkTheme()),
        startY = 0.0f,
        endY = 1500.0f
    )
    Box(
        modifier = Modifier
            .background(gradient, shape = RoundedCornerShape(20.dp))
    ) {
        Text(
            text = bgText,
            modifier = Modifier
                .offset(y = -25.dp)
                .rotate(-10f),
            color = colors.colorPrimary.copy(alpha = 0.2f)
        )
    }
}