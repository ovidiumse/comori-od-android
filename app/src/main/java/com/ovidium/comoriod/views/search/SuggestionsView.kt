import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ovidium.comoriod.model.SearchModel
import com.ovidium.comoriod.ui.theme.Shapes
import com.ovidium.comoriod.ui.theme.getNamedColor
import com.ovidium.comoriod.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SuggestionsView(onItemClick : (String) -> Unit) {
    val isDark = isSystemInDarkTheme()

    val backgroundColor = getNamedColor("Background", isDark)
    val mutedTextColor = getNamedColor("MutedText", isDark)

    val surfaceColor = getNamedColor("Bubble", isDark)
    val textColor = getNamedColor("Text", isDark)

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        Text(
            text = "Sugestii:",
            fontSize = 16.sp,
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
                            fontSize = 12.sp,
                            color = textColor,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable { onItemClick(suggestionsList[index]) }
                        )
                    }
                }
            }
        )
    }
}

