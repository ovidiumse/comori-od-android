package com.ovidium.comoriod.views.library.authors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ovidium.comoriod.R
import com.ovidium.comoriod.model.GoogleSignInModel
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.model.LibraryModelFactory
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.JWTUtils
import com.ovidium.comoriod.utils.fmtVerses
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.search.filter.FilterCategory
import java.net.URLEncoder

@Composable
fun TitlesForAuthorScreen(
    navController: NavController,
    jwtUtils: JWTUtils,
    signInModel: GoogleSignInModel,
    libraryModel: LibraryModel,
    searchParams: SnapshotStateMap<FilterCategory, MutableList<String>>?
) {

    Column() {
            TitlesForAuthorList(libraryModel = libraryModel, navController = navController, searchParams = searchParams, jwtUtils = jwtUtils, signInModel = signInModel)
    }

    LaunchedEffect(Unit) {
        val authors = searchParams?.get(FilterCategory.AUTHORS).let { it } ?: return@LaunchedEffect
        val types = searchParams?.get(FilterCategory.TYPES).let { it } ?: return@LaunchedEffect
        libraryModel.getTitlesForAuthor(authors = authors.joinToString(","), types = types.joinToString(","))
    }

}