import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
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
fun SuggestionsView(coroutineScope: CoroutineScope, keyboardController: SoftwareKeyboardController?, searchModel: SearchModel = viewModel()) {

    var query by remember { searchModel.query }
    var isSearch by remember { searchModel.isSearch }

    Column {
        Text(
            text = "Sugestii:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp)
        )
        LazyColumn {
            items(Constants.suggestionsList.shuffled().take(12).chunked(4)) { items ->
                LazyRow(
                    contentPadding = PaddingValues(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(items) { item ->
                        Text(
                            text = item,
                            fontSize = 12.sp,
                            color = getNamedColor("Link", isDark = isSystemInDarkTheme())!!,
                            maxLines = 1,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier
                                .padding(3.dp)
                                .background(
                                    getNamedColor("Container", isDark = isSystemInDarkTheme())!!,
                                    shape = Shapes.medium
                                )
                                .padding(10.dp)
                                .clickable {
                                    query = item
                                    if (query.isNotEmpty()) {
                                        isSearch = true
                                        coroutineScope.launch {
                                            keyboardController?.hide()
                                            searchModel.search()
                                        }
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}