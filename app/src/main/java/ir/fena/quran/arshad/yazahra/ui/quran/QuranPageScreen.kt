package ir.fena.quran.arshad.yazahra.ui.quran

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ir.fena.quran.arshad.yazahra.allSurahs
import ir.fena.quran.arshad.yazahra.data.AudioDownloader
import ir.fena.quran.arshad.yazahra.data.DatabaseHelper
import ir.fena.quran.arshad.yazahra.data.FontHelper
import ir.fena.quran.arshad.yazahra.data.ReadingHistoryManager
import ir.fena.quran.arshad.yazahra.data.ThemeManager
import ir.fena.quran.arshad.yazahra.data.model.Verse
import ir.fena.quran.arshad.yazahra.data.model.groupVerses
import ir.fena.quran.arshad.yazahra.ui.theme.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** بررسی اتصال اینترنت */
private fun isInternetAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

/** دانلود با تلاش مجدد و مدیریت قطعی اینترنت */
private suspend fun downloadAyahWithRetry(
    context: Context,
    qari: String,
    surahId: Int,
    ayahId: Int,
    maxAttempts: Int = 3,
    onNoInternet: (() -> Unit)? = null
): Boolean {
    val folder = File(context.filesDir, "audio/$qari/${surahId.toString().padStart(3, '0')}")
    val fileName = "${surahId.toString().padStart(3, '0')}${ayahId.toString().padStart(3, '0')}.mp3"
    val file = File(folder, fileName)
    if (file.exists() && file.length() > 0) return true

    var attempt = 0
    while (attempt < maxAttempts) {
        if (!isInternetAvailable(context)) {
            onNoInternet?.invoke()
            return false
        }
        val success = try {
            AudioDownloader.downloadAyah(qari, surahId, ayahId, context)
        } catch (e: Exception) {
            false
        }
        if (success && file.exists() && file.length() > 0) return true

        attempt++
        if (attempt < maxAttempts) delay(800L * attempt)
    }
    return false
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun QuranPageScreen(
    pageId: Int,
    surahId: Int = 0,
    ayahNumber: Int = 0,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun showError(msg: String) {
        coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
    }

    // ---------- SharedPreferences ----------
    val prefs = remember { context.getSharedPreferences("quran_settings", Context.MODE_PRIVATE) }

    var currentPage by remember { mutableIntStateOf(pageId) }
    var verses by remember { mutableStateOf<List<Verse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val db = remember { DatabaseHelper.getInstance(context) }
    val tableName = db.quranTableName

    var totalPages by remember { mutableIntStateOf(604) }
    var barsVisible by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var pendingAutoPlay by remember { mutableStateOf(false) }
    var isFirstLoad by remember { mutableStateOf(true) }
    var fontsReady by remember { mutableStateOf(false) }

    var repeatCount by remember { mutableIntStateOf(prefs.getInt("repeat_count", 0)) }
    var repeatRemaining by remember { mutableIntStateOf(0) }

    var selectedQari by remember { mutableStateOf(prefs.getString("selected_qari", "sh") ?: "sh") }
    val qariList = listOf("sh" to "شاطری", "af" to "عفاسی", "pa" to "پرهیزگار", "mi" to "منشاوی")

    var translationVisible by remember { mutableStateOf(prefs.getBoolean("translation_visible", true)) }

    var playbackSpeed by remember { mutableFloatStateOf(prefs.getFloat("playback_speed", 1.0f)) }

    var quranFontFamily by remember { mutableStateOf(prefs.getString("quran_font", "Taha") ?: "Taha") }
    var translationFontFamily by remember { mutableStateOf(prefs.getString("translation_font", "NoorLotus") ?: "NoorLotus") }
    var tafsirFontFamily by remember { mutableStateOf(prefs.getString("tafsir_font", "NoorLotus") ?: "NoorLotus") }
    var quranFontSize by remember { mutableIntStateOf(prefs.getInt("quran_font_size", 26)) }
    var translationFontSize by remember { mutableIntStateOf(prefs.getInt("translation_font_size", 16)) }
    var tafsirFontSize by remember { mutableIntStateOf(prefs.getInt("tafsir_font_size", 16)) }

    var themeFiles by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedThemeIndex by remember { mutableIntStateOf(prefs.getInt("selected_theme_index", 8)) }

    var currentAyahIndex by remember { mutableIntStateOf(0) }

    // نگهداری Job پیش‌دانلود برای لغو در صورت شروع جدید
    var prefetchJob by remember { mutableStateOf<Job?>(null) }

    // کپی فونت‌ها
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) { FontHelper.copyFontsToFiles(context) }
        fontsReady = true
    }

    // ذخیره تنظیمات
    LaunchedEffect(repeatCount) { prefs.edit().putInt("repeat_count", repeatCount).apply() }
    LaunchedEffect(selectedQari) { prefs.edit().putString("selected_qari", selectedQari).apply() }
    LaunchedEffect(translationVisible) { prefs.edit().putBoolean("translation_visible", translationVisible).apply() }
    LaunchedEffect(playbackSpeed) { prefs.edit().putFloat("playback_speed", playbackSpeed).apply() }
    LaunchedEffect(quranFontFamily) { prefs.edit().putString("quran_font", quranFontFamily).apply() }
    LaunchedEffect(translationFontFamily) { prefs.edit().putString("translation_font", translationFontFamily).apply() }
    LaunchedEffect(tafsirFontFamily) { prefs.edit().putString("tafsir_font", tafsirFontFamily).apply() }
    LaunchedEffect(quranFontSize) { prefs.edit().putInt("quran_font_size", quranFontSize).apply() }
    LaunchedEffect(translationFontSize) { prefs.edit().putInt("translation_font_size", translationFontSize).apply() }
    LaunchedEffect(tafsirFontSize) { prefs.edit().putInt("tafsir_font_size", tafsirFontSize).apply() }
    LaunchedEffect(selectedThemeIndex) { prefs.edit().putInt("selected_theme_index", selectedThemeIndex).apply() }
    LaunchedEffect(currentPage) { prefs.edit().putInt("last_page", currentPage).apply() }
    LaunchedEffect(currentAyahIndex) { prefs.edit().putInt("last_ayah", currentAyahIndex).apply() }

    // دریافت تعداد کل صفحات
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val cursor = db.readableDatabase.rawQuery("SELECT MAX(PageID) FROM $tableName", null)
            if (cursor.moveToFirst()) totalPages = cursor.getInt(0)
            cursor.close()
        }
    }

    // بارگذاری آیات
    LaunchedEffect(currentPage) {
        isLoading = true
        barsVisible = false
        try {
            verses = withContext(Dispatchers.IO) {
                val cursor = db.readableDatabase.rawQuery(
                    "SELECT * FROM $tableName WHERE PageID = ? ORDER BY ParentID, CategoryID",
                    arrayOf(currentPage.toString())
                )
                val list = mutableListOf<Verse>()
                while (cursor.moveToNext()) {
                    list.add(
                        Verse(
                            parentId = cursor.getInt(cursor.getColumnIndexOrThrow("ParentID")),
                            categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("CategoryID")),
                            pageId = cursor.getInt(cursor.getColumnIndexOrThrow("PageID")),
                            juz = cursor.getInt(cursor.getColumnIndexOrThrow("Juz")),
                            title = cursor.getString(cursor.getColumnIndexOrThrow("Title")),
                            comment = cursor.getString(cursor.getColumnIndexOrThrow("Comment")),
                            tafsir = cursor.getString(cursor.getColumnIndexOrThrow("Tafsir")),
                            pavaragi = cursor.getString(cursor.getColumnIndexOrThrow("Pavaragi")),
                            favorite = cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1
                        )
                    )
                }
                cursor.close()
                list
            }
        } catch (e: Exception) { showError("خطای دیتابیس: ${e.message}") }
        isLoading = false
    }

    // گروه‌بندی آیات با الگوریتم اصلاح‌شده (از قبل در QuranModels)
    val groups = remember(verses) { if (verses.isNotEmpty()) groupVerses(verses) else emptyList() }
    val surahName = allSurahs.find { it.number == verses.firstOrNull()?.parentId }?.persianName ?: ""
    val juz = verses.firstOrNull()?.juz ?: 0
    val totalAyahs = allSurahs.find { it.number == verses.firstOrNull()?.parentId }?.ayahCount ?: 0
    val allVerses = groups.flatMap { it.memberVerses }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val themeFilesLoaded = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        themeFiles = withContext(Dispatchers.IO) { ThemeManager.getThemeList(context) }
        themeFilesLoaded.value = true
    }

    val currentHtml = remember(selectedThemeIndex, themeFiles, themeFilesLoaded.value) {
        val raw = if (themeFiles.isNotEmpty()) {
            try { ThemeManager.loadThemeContent(context, themeFiles[selectedThemeIndex]) } catch (_: Exception) { "" }
        } else ""
        val actualFontsPath = context.filesDir.absolutePath + "/fonts/"
        raw.replace(
            "file:///data/data/ir.fena.quran.arshad.yazahra/files/fonts/",
            "file://$actualFontsPath"
        )
    }

    LaunchedEffect(barsVisible) {
        if (barsVisible) {
            delay(5000)
            barsVisible = false
        }
    }

    // ✅ اصلاح اصلی: در ساخت JSON تفسیر را تریم می‌کنیم
    val groupsJson = remember(groups, translationVisible, currentPage) {
        try {
            JSONArray().apply {
                groups.forEach { g ->
                    put(JSONObject().apply {
                        val headTafsir = g.headVerse.tafsir.trim()
                        put("tafsir", if (headTafsir.isNotEmpty() && !headTafsir.startsWith("#")) headTafsir else "")
                        put("verses", JSONArray().apply {
                            g.memberVerses.forEach { v ->
                                val surah = allSurahs.find { it.number == v.parentId }
                                put(JSONObject().apply {
                                    put("surahId", v.parentId)
                                    put("categoryId", v.categoryId)
                                    put("title", v.title)
                                    put("translation", if (translationVisible) v.comment.trim() else "")
                                    put("ayahNumber", v.categoryId)
                                    put("ayahCount", surah?.ayahCount ?: 0)
                                    put("surahName", surah?.persianName ?: "")
                                    put("pageId", v.pageId)
                                    put("favorite", v.favorite)
                                })
                            }
                        })
                    })
                }
            }.toString()
        } catch (e: Exception) { "[]" }
    }

    fun injectData() {
        webView?.evaluateJavascript(
            "if(typeof renderQuran === 'function') renderQuran(" +
                    "$groupsJson, $translationVisible, " +
                    "'$quranFontFamily', '$translationFontFamily', '$tafsirFontFamily', " +
                    "$quranFontSize, $translationFontSize, $tafsirFontSize);",
            null
        )
    }

    // پخش آیه
    fun playAyah(index: Int, includeBismillah: Boolean = true) {
        if (index < 0 || index >= allVerses.size) return
        coroutineScope.launch {
            try {
                if (isPlaying) {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    isPlaying = false
                }
                val ayah = allVerses[index]
                val qari = selectedQari

                // بسم‌الله
                if (includeBismillah && ayah.categoryId == 1 && ayah.parentId != 1 && ayah.parentId != 9) {
                    val downloaded = withContext(Dispatchers.IO) {
                        downloadAyahWithRetry(
                            context = context,
                            qari = qari,
                            surahId = 1,
                            ayahId = 1,
                            onNoInternet = { showError("اتصال اینترنت برقرار نیست. برای پخش آنلاین به اینترنت نیاز دارید.") }
                        )
                    }
                    if (!downloaded) { showError("دانلود صوت بسم‌الله ناموفق بود"); return@launch }
                    val folder = File(context.filesDir, "audio/$qari/001")
                    val file = File(folder, "001001.mp3")
                    val mp = MediaPlayer().apply {
                        setDataSource(file.absolutePath)
                        prepare()
                        val params = playbackParams ?: PlaybackParams()
                        params.speed = playbackSpeed
                        playbackParams = params
                        setOnCompletionListener {
                            isPlaying = false; release(); mediaPlayer = null
                            playAyah(index, includeBismillah = false)
                        }
                        start()
                    }
                    mediaPlayer = mp; isPlaying = true
                    return@launch
                }

                // دانلود آیه اصلی
                val downloaded = withContext(Dispatchers.IO) {
                    downloadAyahWithRetry(
                        context = context,
                        qari = qari,
                        surahId = ayah.parentId,
                        ayahId = ayah.categoryId,
                        onNoInternet = { showError("اتصال اینترنت برقرار نیست. برای پخش آنلاین به اینترنت نیاز دارید.") }
                    )
                }
                if (!downloaded) { showError("دانلود صوت ناموفق بود"); return@launch }

                // پیش‌دانلود ۳ آیه بعد
                prefetchJob?.cancel()
                prefetchJob = coroutineScope.launch(Dispatchers.IO) {
                    for (i in 1..3) {
                        val nextIndex = index + i
                        if (nextIndex < allVerses.size) {
                            val next = allVerses[nextIndex]
                            val file = File(context.filesDir, "audio/$qari/${next.parentId.toString().padStart(3, '0')}/${next.parentId.toString().padStart(3, '0')}${next.categoryId.toString().padStart(3, '0')}.mp3")
                            if (!file.exists() || file.length() == 0L) {
                                downloadAyahWithRetry(context, qari, next.parentId, next.categoryId)
                            }
                        }
                    }
                }

                val folder = File(context.filesDir, "audio/$qari/${ayah.parentId.toString().padStart(3, '0')}")
                val fileName = "${ayah.parentId.toString().padStart(3, '0')}${ayah.categoryId.toString().padStart(3, '0')}.mp3"
                val file = File(folder, fileName)

                val mp = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    val params = playbackParams ?: PlaybackParams()
                    params.speed = playbackSpeed
                    playbackParams = params
                    setOnCompletionListener {
                        isPlaying = false; release(); mediaPlayer = null
                        if (repeatRemaining > 0) {
                            repeatRemaining--
                            playAyah(index, includeBismillah = true)
                        } else {
                            if (index + 1 < allVerses.size) {
                                currentAyahIndex = index + 1
                                repeatRemaining = repeatCount
                                playAyah(currentAyahIndex, includeBismillah = true)
                            } else {
                                val nextPage = if (currentPage < totalPages) currentPage + 1 else 1
                                currentPage = nextPage
                                currentAyahIndex = 0
                                pendingAutoPlay = true
                            }
                        }
                    }
                    start()
                }
                mediaPlayer = mp; isPlaying = true
                webView?.evaluateJavascript("highlightAyah(${ayah.parentId}, ${ayah.categoryId})", null)
            } catch (e: Exception) { showError("پخش خطا: ${e.message}") }
        }
    }

    LaunchedEffect(playbackSpeed) {
        mediaPlayer?.let { mp ->
            val params = mp.playbackParams ?: PlaybackParams()
            params.speed = playbackSpeed
            mp.playbackParams = params
        }
    }

    // پخش خودکار بعد از بارگذاری صفحه
    LaunchedEffect(verses) {
        if (verses.isNotEmpty()) {
            if (surahId != 0 && ayahNumber != 0) {
                val targetIndex = allVerses.indexOfFirst { it.parentId == surahId && it.categoryId == ayahNumber }
                if (targetIndex != -1) currentAyahIndex = targetIndex else currentAyahIndex = 0
                pendingAutoPlay = true
            }
            if (pendingAutoPlay) {
                pendingAutoPlay = false
                delay(300)
                repeatRemaining = repeatCount
                playAyah(currentAyahIndex, includeBismillah = true)
            }
        }
    }

    LaunchedEffect(currentHtml) {
        if (!isFirstLoad) {
            webView?.loadDataWithBaseURL("file://${context.filesDir}/", currentHtml, "text/html", "UTF-8", null)
        }
        isFirstLoad = false
    }

    LaunchedEffect(groupsJson, translationVisible, quranFontFamily, translationFontFamily,
        tafsirFontFamily, quranFontSize, translationFontSize, tafsirFontSize) {
        injectData()
    }

    val fontSizeOptions = remember { (10..110 step 2).toList() }

    fun addHistoryEntry() {
        val currentVerse = allVerses.getOrNull(currentAyahIndex)
        if (currentVerse != null) {
            ReadingHistoryManager.addHistoryEntry(
                context = context,
                surahId = currentVerse.parentId,
                surahName = allSurahs.find { it.number == currentVerse.parentId }?.persianName ?: "",
                ayahNumber = currentVerse.categoryId,
                pageId = currentPage
            )
        }
    }

    fun exitScreen() {
        if (isPlaying) {
            mediaPlayer?.stop(); mediaPlayer?.release(); mediaPlayer = null; isPlaying = false
        }
        addHistoryEntry()
        onBack()
    }

    BackHandler { exitScreen() }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, modifier = Modifier.fillMaxSize()) { padding ->
        if (isLoading || !fontsReady) {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                            barsVisible = true
                        }
                    }
                }
        ) {
            Column(Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = barsVisible,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Surface(
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { exitScreen() }) {
                                Text("\uF060", fontFamily = FontAwesome, fontSize = 20.sp)
                                Spacer(Modifier.width(4.dp))
                                Text("بازگشت")
                            }
                            Spacer(Modifier.weight(1f))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("صفحه $currentPage | جزء $juz", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "سوره $surahName (آیه ${currentAyahIndex + 1} از $totalAyahs)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            if (themeFiles.isNotEmpty()) {
                                var expanded by remember { mutableStateOf(false) }
                                Box {
                                    TextButton(onClick = { expanded = true }) {
                                        Text("\uF1FC", fontFamily = FontAwesome, fontSize = 18.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(themeFiles[selectedThemeIndex].removeSuffix(".html"))
                                    }
                                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                        themeFiles.forEachIndexed { index, file ->
                                            DropdownMenuItem(
                                                text = { Text(file.removeSuffix(".html")) },
                                                onClick = { selectedThemeIndex = index; expanded = false }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.allowFileAccess = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    webView = view
                                    injectData()
                                    if (pendingAutoPlay) {
                                        pendingAutoPlay = false
                                        coroutineScope.launch {
                                            delay(100)
                                            repeatRemaining = repeatCount
                                            playAyah(currentAyahIndex, includeBismillah = true)
                                        }
                                    }
                                }
                                override fun onReceivedError(
                                    view: WebView?,
                                    errorCode: Int,
                                    description: String?,
                                    failingUrl: String?
                                ) {
                                    super.onReceivedError(view, errorCode, description, failingUrl)
                                    showError("خطای بارگذاری تم: $description")
                                }
                            }
                            addJavascriptInterface(object {
                                @JavascriptInterface fun showTafsir(tafsirText: String) {
                                    if (isPlaying) {
                                        mediaPlayer?.stop(); mediaPlayer?.release(); mediaPlayer = null; isPlaying = false
                                    }
                                }
                                @JavascriptInterface fun playFromAyah(surahId: Int, categoryId: Int) {
                                    val idx = allVerses.indexOfFirst { it.parentId == surahId && it.categoryId == categoryId }
                                    if (idx != -1) {
                                        currentAyahIndex = idx
                                        repeatRemaining = repeatCount
                                        playAyah(idx, includeBismillah = true)
                                    }
                                }
                                @JavascriptInterface fun toggleFavorite(surahId: Int, categoryId: Int) {
                                    coroutineScope.launch {
                                        val newState = withContext(Dispatchers.IO) { db.toggleFavorite(surahId, categoryId) }
                                        webView?.evaluateJavascript("setHeartState($surahId, $categoryId, $newState)", null)
                                    }
                                }
                            }, "Android")
                            loadDataWithBaseURL("file://${ctx.filesDir}/", currentHtml, "text/html", "UTF-8", null)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                AnimatedVisibility(
                    visible = barsVisible,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Surface(
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        Column(Modifier.padding(vertical = 6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                IconButton(onClick = {
                                    if (currentAyahIndex > 0) { currentAyahIndex--; repeatRemaining = repeatCount; playAyah(currentAyahIndex, includeBismillah = true) }
                                }) { Text("\uF049", fontFamily = FontAwesome, fontSize = 28.sp) }
                                IconButton(onClick = {
                                    if (isPlaying) { mediaPlayer?.pause(); isPlaying = false }
                                    else if (mediaPlayer != null) { mediaPlayer?.start(); isPlaying = true }
                                    else { repeatRemaining = repeatCount; playAyah(currentAyahIndex, includeBismillah = true) }
                                }) { Text(if (isPlaying) "\uF04C" else "\uF04B", fontFamily = FontAwesome, fontSize = 36.sp) }
                                IconButton(onClick = {
                                    if (currentAyahIndex < allVerses.size - 1) { currentAyahIndex++; repeatRemaining = repeatCount; playAyah(currentAyahIndex, includeBismillah = true) }
                                }) { Text("\uF050", fontFamily = FontAwesome, fontSize = 28.sp) }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                                var speedExpanded by remember { mutableStateOf(false) }
                                Box {
                                    TextButton(onClick = { speedExpanded = true }) { Text("${playbackSpeed}x") }
                                    DropdownMenu(expanded = speedExpanded, onDismissRequest = { speedExpanded = false }) {
                                        listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { s ->
                                            DropdownMenuItem(text = { Text("${s}x") }, onClick = { playbackSpeed = s; speedExpanded = false })
                                        }
                                    }
                                }
                                IconButton(onClick = { repeatCount = (repeatCount + 1) % 8 }) {
                                    Text(if (repeatCount == 0) "\uF021" else repeatCount.toString(),
                                        fontFamily = if (repeatCount == 0) FontAwesome else NoorLotus,
                                        fontSize = 22.sp,
                                        color = if (repeatCount > 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface)
                                }
                                var qariExpanded by remember { mutableStateOf(false) }
                                Box {
                                    TextButton(onClick = { qariExpanded = true }) {
                                        Text(qariList.find { it.first == selectedQari }?.second ?: selectedQari)
                                    }
                                    DropdownMenu(expanded = qariExpanded, onDismissRequest = { qariExpanded = false }) {
                                        qariList.forEach { (code, name) ->
                                            DropdownMenuItem(text = { Text(name) }, onClick = { selectedQari = code; qariExpanded = false })
                                        }
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                                IconButton(onClick = { translationVisible = !translationVisible }) {
                                    Text(if (translationVisible) "\uF06E" else "\uF070", fontFamily = FontAwesome, fontSize = 20.sp)
                                }
                                var quranFontExpanded by remember { mutableStateOf(false) }
                                Box {
                                    TextButton(onClick = { quranFontExpanded = true }) { Text("ق:$quranFontFamily", style = MaterialTheme.typography.labelSmall) }
                                    DropdownMenu(expanded = quranFontExpanded, onDismissRequest = { quranFontExpanded = false }) {
                                        listOf("NoorZar", "Scheherazade", "Amiri", "Taha").forEach { font ->
                                            DropdownMenuItem(text = { Text(font) }, onClick = { quranFontFamily = font; quranFontExpanded = false })
                                        }
                                    }
                                }
                                var translationFontExpanded by remember { mutableStateOf(false) }
                                Box {
                                    TextButton(onClick = { translationFontExpanded = true }) { Text("ت:$translationFontFamily", style = MaterialTheme.typography.labelSmall) }
                                    DropdownMenu(expanded = translationFontExpanded, onDismissRequest = { translationFontExpanded = false }) {
                                        listOf("NoorLotus", "NoorZar", "Taha").forEach { font ->
                                            DropdownMenuItem(text = { Text(font) }, onClick = { translationFontFamily = font; translationFontExpanded = false })
                                        }
                                    }
                                }
                                var quranSizeExpanded by remember { mutableStateOf(false) }
                                Box {
                                    TextButton(onClick = { quranSizeExpanded = true }) { Text("${quranFontSize}px", style = MaterialTheme.typography.labelSmall) }
                                    DropdownMenu(expanded = quranSizeExpanded, onDismissRequest = { quranSizeExpanded = false }) {
                                        fontSizeOptions.forEach { size ->
                                            DropdownMenuItem(text = { Text("$size") }, onClick = { quranFontSize = size; quranSizeExpanded = false })
                                        }
                                    }
                                }
                                var translationSizeExpanded by remember { mutableStateOf(false) }
                                Box {
                                    TextButton(onClick = { translationSizeExpanded = true }) { Text("${translationFontSize}px", style = MaterialTheme.typography.labelSmall) }
                                    DropdownMenu(expanded = translationSizeExpanded, onDismissRequest = { translationSizeExpanded = false }) {
                                        fontSizeOptions.forEach { size ->
                                            DropdownMenuItem(text = { Text("$size") }, onClick = { translationFontSize = size; translationSizeExpanded = false })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}