package com.ovidium.comoriod.views.library.bible

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.ovidium.comoriod.R

@Composable
fun CommonBottomSheetHeader() {
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Surface(
            color = colorResource(R.color.colorDarkPrimary),
            modifier = Modifier
                .width(41.dp)
                .height(5.dp)
                .align(Alignment.TopCenter)
        ) {}

    }
}