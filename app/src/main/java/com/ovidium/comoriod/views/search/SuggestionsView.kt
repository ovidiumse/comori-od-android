import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.components.AdaptiveText
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SuggestionsView(onItemClick: (String) -> Unit) {
    val isDark = isSystemInDarkTheme()

    val captionStyle = MaterialTheme.typography.caption
    var suggestionTextStyle by remember { mutableStateOf(captionStyle) }
    var suggestionTextStyleReady by remember { mutableStateOf(false) }

    val backgroundColor = getNamedColor("Background", isDark)
    val mutedTextColor = getNamedColor("MutedText", isDark)
    val surfaceColor = getNamedColor("Bubble", isDark)
    val textColor = getNamedColor("Text", isDark)

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        AdaptiveText(
            text = "Sugestii:",
            minFontSize = 14.sp,
            maxFontSize = 28.sp,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = mutedTextColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(90.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                val suggestionsList = Constants.suggestionsList.shuffled().take(16)
                items(suggestionsList.size) { index ->
                    Card(backgroundColor = surfaceColor, modifier = Modifier.wrapContentWidth()) {
                        Text(
                            text = suggestionsList[index],
                            style = suggestionTextStyle,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(10.dp)
                                .drawWithContent { if (suggestionTextStyleReady) drawContent() }
                                .clickable { onItemClick(suggestionsList[index]) },
                            onTextLayout = {textLayoutResult ->
                                if (textLayoutResult.didOverflowHeight || textLayoutResult.didOverflowWidth)
                                    suggestionTextStyle = suggestionTextStyle.copy(fontSize = suggestionTextStyle.fontSize * 0.9)
                                else
                                    suggestionTextStyleReady = true
                            }
                        )
                    }
                }
            }
        )
    }
}

