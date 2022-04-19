import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SuggestionsView(coroutineScope: CoroutineScope, keyboardController: SoftwareKeyboardController?) {

    val searchModel: SearchModel = viewModel()
    var query by remember { searchModel.query }
    var isSearch by remember { searchModel.isSearch }
    val suggestionsList = listOf<String>(
        "Dumnezeu",
        "Ajutor",
        "Iertare",
        "Smerenie",
        "Indurare",
        "Suferinta",
        "Vindecare",
        "Lumina",
        "Minune",
        "Jertfa",
        "Adevar",
        "Alinare",
        "Cuvant",
        "Paine",
        "Hristos",
        "Legamant",
        "Binefacere",
        "Invatatura",
        "Prieten",
        "Botez",
        "Sfant",
        "Duh",
        "Cununie",
        "Sfarsit",
        "Inceput",
        "Potir",
        "Biserica",
        "Bogatie",
        "Sarac",
        "Popor",
        "Taina",
        "Mangaiere",
        "Parinte",
        "Vestire",
        "Aflare",
        "Viata",
        "Moarte",
        "Nastere",
        "Frate",
        "Mama",
        "Tata",
        "Sora",
        "Pagan",
        "Credincios",
        "Cruce",
        "Golgota",
        "Asternut",
        "Roua",
        "Cina",
        "Apa",
        "Pamant",
        "Sange",
        "Dragoste",
        "Iubire",
        "Rabdare",
        "Pustiu",
        "Inger",
        "Mantuitor",
        "Isus",
        "Ocara",
        "Argint",
        "Aur",
        "Lemn",
        "Bucurie",
        "Nadejde",
        "Mandrie",
        "Minciuna",
        "Batran",
        "Tanar",
        "Har",
        "Bun",
        "Rau",
        "Lacrimi",
        "Vesnic",
        "Judecata",
        "Nou",
        "Vechi",
        "Ascultare",
        "Maica",
        "Domnul",
        "Mire",
        "Mireasa",
        "Logodna",
        "Viu",
        "Rece",
        "Fierbinte",
        "Genunchi",
        "Curat",
        "Intinat",
        "Cer",
        "Intelept",
        "Intelepciune",
        "Ura",
        "Dar",
        "Biruinta",
        "Infrangere",
        "Neam",
        "Vrajmas",
        "Legat",
        "Legatura",
        "Cunostinta",
        "Ingamfa",
        "Vorbire",
        "Tacere",
        "Mult",
        "Putin",
        "Inchinare",
        "Rugaciune",
        "Fericit",
        "Fericire",
        "Aproape",
        "Departe",
        "Intuneric",
        "Sus",
        "Jos",
        "Sarut",
        "Inima",
        "Valoare",
        "Rasplata",
        "Odihna",
        "Inviere",
        "Omul",
        "Invatator",
        "Invatatura",
        "Lupta",
        "Infrant",
        "Infranare",
        "Post",
        "Plans",
        "Stralucire",
        "Intinare",
        "Cantare",
        "Cerere",
        "Etern",
        "Legat",
        "Legatura",
        "Slobod",
        "Slobozenie",
        "Clocot",
        "Constiinta",
        "Cuget",
        "Gand",
        "Suflet",
        "Pace",
        "Razboi",
        "Chemare",
        "Vremea",
        "Timpul",
        "Ales",
        "Alegere",
        "Lauda",
        "Rusinea",
        "Mort",
        "Nor",
        "Soare",
        "Martor",
        "Marturisitor",
        "Lepadat",
        "Lepadare",
        "Cuviinta",
        "Evlavie",
        "Evlavios",
        "Crestin",
        "Ostas",
        "Zadarnic",
        "Zadarnicie",
        "Pierdere",
        "Pierdut",
        "Vina",
        "Vinovat",
        "Osanda",
        "Blestem",
        "Binecuvantare",
        "Inselat",
        "Inselaciune",
        "Dulce",
        "Amar",
        "Copil",
        "Slab",
        "Tare",
        "Slabiciune",
        "Unitate",
        "Dezbinare",
        "Unire",
        "Frica",
        "Despartire",
        "Curaj",
        "Fuga",
        "Flamand",
        "Desfranare",
        "Imbuibare",
        "Cazut",
        "Intors",
        "Intoarcere",
        "Hotarat",
        "Hotarare",
        "Meditare",
        "Meditatie",
        "Poezie",
        "Citire",
        "Trezire",
        "Rod",
        "Cules",
        "Adunare",
        "Altar",
        "Apus",
        "Rasarit",
        "Ridicat",
        "Bland",
        "Blandete",
        "Afara",
        "Inauntru",
        "Gol",
        "Acoperit",
        "Bolnav",
        "Sanatos",
        "Lacom",
        "Lacomie",
        "Predestinare",
        "Prooroci",
        "Prorocii",
        "Chin",
        "Cautare",
        "Ajuns",
        "Ajungere",
        "Plin",
        "Murdar",
        "Supus",
        "Supunere"
    )

    Column {
        Text(
            text = "Sugestii:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        )
        LazyColumn {
            items(suggestionsList.take(12).shuffled().chunked(4)) { items ->
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