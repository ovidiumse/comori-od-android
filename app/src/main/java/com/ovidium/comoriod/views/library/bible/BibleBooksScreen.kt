package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ovidium.comoriod.data.bible.BibleBook
import com.ovidium.comoriod.model.LibraryModel
import com.ovidium.comoriod.ui.theme.Montserrat
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.views.Screens
import com.ovidium.comoriod.views.library.StateHandler
import java.net.URLEncoder

@Composable
fun BibleBooksScreen(navController: NavHostController, libraryModel: LibraryModel) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StateHandler(navController, libraryModel.bibleBooksData) { data, _ ->
            Text(
                text = "Biblia",
                fontSize = 36.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 4.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                contentPadding = PaddingValues(5.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                data?.let {
                    items(data.oldTestamentBooks + data.newTestamentBooks) { item: BibleBook ->
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .border(1.dp, if (data.newTestamentBooks.contains(item)) Color.Green else getNamedColor("Link", isSystemInDarkTheme()), RoundedCornerShape(12.dp))
                                .clickable {
                                    navController.navigate(Screens.BibleBooks.withArgs(URLEncoder.encode(item.name, "utf-8")))
                                },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = item.shortName,
                                fontSize = 10.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .padding(horizontal = 6.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

}