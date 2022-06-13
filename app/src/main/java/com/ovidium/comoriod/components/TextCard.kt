package com.ovidium.comoriod.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ovidium.comoriod.R
import com.ovidium.comoriod.ui.theme.getNamedColor

@Composable
fun TextCard(
    index: Int? = null,
    title: AnnotatedString,
    book: String,
    author: String,
    type: String,
    text: AnnotatedString,
    subtitleColor: Color,
    bgColor: Color,
    bubbleColor: Color,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable {
                onItemClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.Start)
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    if (index != null)
                        withStyle(style = SpanStyle(color = subtitleColor)) {
                            append("${index + 1}.  ")
                        }

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(title)
                    }
                },
                fontSize = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Left
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 5.dp)
                .wrapContentSize(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_outline_menu_book_24),
                        contentDescription = "Book icon",
                        tint = subtitleColor
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = book,
                        style = MaterialTheme.typography.caption,
                        color = subtitleColor,
                        modifier = Modifier
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_author),
                        contentDescription = "Author icon",
                        tint = subtitleColor
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = author,
                        style = MaterialTheme.typography.caption,
                        color = subtitleColor,
                        modifier = Modifier
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TagBubble(tag = type, textColor = subtitleColor, bubbleColor = bubbleColor)
            }
        }

        if (text.isNotEmpty()) {
            Divider()
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp, bottom = 16.dp)
            )
        } else
            Spacer(modifier = Modifier.height(11.dp))
    }
}

@Composable
@Preview
fun TextCardPreview() {
    val isDark = isSystemInDarkTheme()

    TextCard(
        title = buildAnnotatedString { append("Constiinta misiunii noastre") },
        book = "Semanati Cuvantul Sfant",
        author = "Traian Dorz",
        type = "articol",
        text = buildAnnotatedString { append("Conştiinţa colectivităţii noastre evanghelice trebuie să determine toată orientarea şi activitatea noastră. Să depăşim - cum s-ar zice stadiul egoist al proprietăţii şi intereselor, iubind, trecând la nivelul înalt, altruist şi superior al părtăşiei, al intereselor şi proprietăţii noastre frăţeşti, în specificul divin al marii Evanghelii în care ne-a încadrat Hristos, Domnul nostru.") },
        subtitleColor = getNamedColor("MutedText", isDark),
        bgColor = getNamedColor("PrimarySurface", isDark),
        bubbleColor = getNamedColor("Bubble", isDark)
    ) {

    }
}