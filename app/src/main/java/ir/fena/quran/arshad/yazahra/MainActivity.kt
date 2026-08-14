package ir.fena.quran.arshad.yazahra

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import ir.fena.quran.arshad.yazahra.data.DatabaseHelper
import ir.fena.quran.arshad.yazahra.ui.about.AboutScreen
import ir.fena.quran.arshad.yazahra.ui.contact.ContactScreen
import ir.fena.quran.arshad.yazahra.ui.download.DownloadScreen
import ir.fena.quran.arshad.yazahra.ui.estekhare.EstekhareScreen
import ir.fena.quran.arshad.yazahra.ui.favorites.FavoritesScreen
import ir.fena.quran.arshad.yazahra.ui.history.ReadingHistoryScreen
import ir.fena.quran.arshad.yazahra.ui.home.HomeScreen
import ir.fena.quran.arshad.yazahra.ui.juz.JuzSelectionScreen
import ir.fena.quran.arshad.yazahra.ui.page.PageSelectionScreen
import ir.fena.quran.arshad.yazahra.ui.quran.QuranPageScreen
import ir.fena.quran.arshad.yazahra.ui.theme.YaZahra_Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val savedTheme = prefs.getBoolean("dark_mode", isSystemInDarkTheme())
            var isDark by remember { mutableStateOf(savedTheme) }

            LaunchedEffect(isDark) {
                prefs.edit().putBoolean("dark_mode", isDark).apply()
            }

            val dbHelper = remember { DatabaseHelper.getInstance(this@MainActivity) }

            var currentScreen by remember { mutableStateOf("home") }
            var selectedSurah by remember { mutableStateOf<QuranSurah?>(null) }
            var selectedAyahNumber by remember { mutableIntStateOf(0) }
            var currentPageId by remember { mutableIntStateOf(0) }

            BackHandler(enabled = currentScreen != "home") {
                currentScreen = "home"
            }

            YaZahra_Theme(darkTheme = isDark) {
                when (currentScreen) {
                    "home" -> HomeScreen(
                        isDarkMode = isDark,
                        onToggleTheme = { isDark = !isDark },
                        onSurahClick = { currentScreen = "surahSelection" },
                        onJuzClick = { currentScreen = "juzSelection" },
                        onPageClick = { currentScreen = "pageSelection" },
                        onContinueReading = { currentScreen = "history" },
                        onHistoryClick = { currentScreen = "history" },
                        onEstekhareClick = { currentScreen = "estekhare" },
                        onFavoritesClick = { currentScreen = "favorites" },
                        onDownloadClick = { currentScreen = "download" },   // 🆕
                        onAboutClick = { currentScreen = "about" },
                        onContactClick = { currentScreen = "contact" }     // 🆕 بازگشت
                    )

                    "surahSelection" -> SurahSelectionScreen(
                        onSurahSelected = { surah ->
                            selectedSurah = surah
                            currentScreen = "ayahSelection"
                        },
                        onBack = { currentScreen = "home" }
                    )

                    "ayahSelection" -> {
                        selectedSurah?.let { surah ->
                            AyahSelectionScreen(
                                surah = surah,
                                onAyahSelected = { ayahNumber ->
                                    selectedAyahNumber = ayahNumber
                                    currentPageId = dbHelper.getPageIdForAyah(surah.number, ayahNumber)
                                    currentScreen = "quranPage"
                                },
                                onBack = { currentScreen = "surahSelection" }
                            )
                        }
                    }

                    "juzSelection" -> JuzSelectionScreen(
                        onJuzSelected = { surahId, ayahNumber ->
                            selectedSurah = allSurahs.find { it.number == surahId }
                            selectedAyahNumber = ayahNumber
                            currentPageId = dbHelper.getPageIdForAyah(surahId, ayahNumber)
                            currentScreen = "quranPage"
                        },
                        onBack = { currentScreen = "home" }
                    )

                    "pageSelection" -> PageSelectionScreen(
                        onPageSelected = { surahId, ayahNumber, pageId ->
                            selectedSurah = allSurahs.find { it.number == surahId }
                            selectedAyahNumber = ayahNumber
                            currentPageId = pageId
                            currentScreen = "quranPage"
                        },
                        onBack = { currentScreen = "home" }
                    )

                    "quranPage" -> QuranPageScreen(
                        pageId = currentPageId,
                        surahId = selectedSurah?.number ?: 0,
                        ayahNumber = selectedAyahNumber,
                        onBack = { currentScreen = "home" }
                    )

                    "history" -> ReadingHistoryScreen(
                        onBack = { currentScreen = "home" },
                        onItemClick = { surahId, ayahNumber, pageId ->
                            selectedSurah = allSurahs.find { it.number == surahId }
                            selectedAyahNumber = ayahNumber
                            currentPageId = pageId
                            currentScreen = "quranPage"
                        }
                    )

                    "estekhare" -> EstekhareScreen(
                        onBack = { currentScreen = "home" }
                    )

                    "favorites" -> FavoritesScreen(
                        onBack = { currentScreen = "home" },
                        onFavoriteClick = { surahId, ayahNumber ->
                            selectedSurah = allSurahs.find { it.number == surahId }
                            selectedAyahNumber = ayahNumber
                            currentPageId = dbHelper.getPageIdForAyah(surahId, ayahNumber)
                            currentScreen = "quranPage"
                        }
                    )

                    "about" -> AboutScreen(
                        onBack = { currentScreen = "home" }
                    )

                    "contact" -> ContactScreen(
                        onBack = { currentScreen = "home" }
                    )

                    "download" -> DownloadScreen(
                        onBack = { currentScreen = "home" }
                    )
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}