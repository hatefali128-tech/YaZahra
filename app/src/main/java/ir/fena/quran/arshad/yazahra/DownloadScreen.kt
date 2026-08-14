package ir.fena.quran.arshad.yazahra.ui.download

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.fena.quran.arshad.yazahra.allSurahs
import ir.fena.quran.arshad.yazahra.ui.theme.FontAwesome
import ir.fena.quran.arshad.yazahra.ui.theme.NoorLotus
import ir.fena.quran.arshad.yazahra.ui.theme.NoorZar
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

data class AudioFileItem(
    val id: Int,
    val surahId: Int,
    val ayahNumber: Int,
    val fileName: String,
    val localFile: File,
    val isDownloaded: Boolean = false,
    val isDownloading: Boolean = false
) {
    val displayName: String get() = "سوره $surahId آیه $ayahNumber"
}

@Composable
fun DownloadScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedQari by remember { mutableStateOf("sh") }
    val qariList = listOf("sh" to "شاطری", "af" to "عفاسی", "pa" to "پرهیزگار", "mi" to "منشاوی")

    val allFiles = remember { mutableStateListOf<AudioFileItem>() }
    var isDownloading by remember { mutableStateOf(false) }
    val stopFlag = remember { AtomicBoolean(false) }

    fun loadFiles(qari: String) {
        allFiles.clear()
        for (surah in allSurahs) {
            for (ayah in 1..surah.ayahCount) {
                val surahStr = surah.number.toString().padStart(3, '0')
                val ayahStr = ayah.toString().padStart(3, '0')
                val fileName = "$surahStr$ayahStr.mp3"
                val folder = File(context.filesDir, "audio/$qari/$surahStr")
                val localFile = File(folder, fileName)
                allFiles.add(
                    AudioFileItem(
                        id = surah.number * 1000 + ayah,
                        surahId = surah.number,
                        ayahNumber = ayah,
                        fileName = fileName,
                        localFile = localFile,
                        isDownloaded = localFile.exists()
                    )
                )
            }
        }
    }

    LaunchedEffect(selectedQari) {
        loadFiles(selectedQari)
    }

    fun downloadFile(item: AudioFileItem): Boolean {
        val surahStr = item.surahId.toString().padStart(3, '0')
        val urls = listOf(
            "http://quran.1fe.ir/$selectedQari/$surahStr/${item.fileName}",
            "https://quran.1fe.ir/$selectedQari/$surahStr/${item.fileName}"
        )
        for (urlStr in urls) {
            if (stopFlag.get()) return false
            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 15000
                connection.setRequestProperty("User-Agent", "YaZahraApp")
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    item.localFile.parentFile?.mkdirs()
                    BufferedInputStream(connection.inputStream).use { input ->
                        FileOutputStream(item.localFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    return true
                }
            } catch (_: Exception) {
            }
        }
        return false
    }

    fun startDownload() {
        if (isDownloading) return
        isDownloading = true
        stopFlag.set(false)

        coroutineScope.launch(Dispatchers.IO) {
            val pending = allFiles.filter { !it.isDownloaded && !it.isDownloading }
            val executor = Executors.newFixedThreadPool(
                maxOf(12, Runtime.getRuntime().availableProcessors() * 4)
            )

            val tasks = pending.map { item ->
                executor.submit {
                    if (stopFlag.get()) return@submit

                    val index = allFiles.indexOfFirst { it.id == item.id }
                    if (index != -1) {
                        allFiles[index] = allFiles[index].copy(isDownloading = true)
                    }

                    val success = downloadFile(item)

                    val newIndex = allFiles.indexOfFirst { it.id == item.id }
                    if (newIndex != -1) {
                        allFiles[newIndex] = allFiles[newIndex].copy(
                            isDownloading = false,
                            isDownloaded = success && item.localFile.exists()
                        )
                    }
                }
            }

            tasks.forEach { it.get() }
            executor.shutdown()
            isDownloading = false
        }
    }

    fun stopDownload() {
        stopFlag.set(true)
        isDownloading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("\uF060", fontFamily = FontAwesome, fontSize = 20.sp)
                Spacer(Modifier.width(4.dp))
                Text("بازگشت", fontFamily = NoorLotus)
            }
            Text(
                "دانلود صوت قرآن",
                fontFamily = NoorZar,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            qariList.forEach { (code, name) ->
                TextButton(
                    onClick = { selectedQari = code },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        name,
                        fontFamily = if (selectedQari == code) NoorZar else NoorLotus,
                        fontWeight = if (selectedQari == code) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedQari == code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { startDownload() },
                enabled = !isDownloading,
                modifier = Modifier.weight(1f)
            ) {
                Text("دانلود همه", fontFamily = NoorZar)
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { stopDownload() },
                enabled = isDownloading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f)
            ) {
                Text("توقف", fontFamily = NoorZar)
            }
        }

        Spacer(Modifier.height(16.dp))

        val downloadedCount = allFiles.count { it.isDownloaded }
        val downloadingCount = allFiles.count { it.isDownloading }
        val pendingCount = allFiles.count { !it.isDownloaded && !it.isDownloading }

        Text(
            "دانلود شده: $downloadedCount از ${allFiles.size}",
            fontFamily = NoorLotus,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = if (allFiles.isEmpty()) 0f else (downloadedCount.toFloat() / allFiles.size),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // 🆕 سه ستون کنار هم
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DownloadPanel(
                title = "دانلود شده",
                count = downloadedCount,
                files = allFiles.filter { it.isDownloaded },
                headerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            DownloadPanel(
                title = "در حال دانلود",
                count = downloadingCount,
                files = allFiles.filter { it.isDownloading },
                headerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            DownloadPanel(
                title = "باقی‌مانده",
                count = pendingCount,
                files = allFiles.filter { !it.isDownloaded && !it.isDownloading },
                headerColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DownloadPanel(
    title: String,
    count: Int,
    files: List<AudioFileItem>,
    headerColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        Text(
            "$title ($count)",
            fontFamily = NoorZar,
            fontWeight = FontWeight.Bold,
            color = headerColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        )
        if (files.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "موردی نیست",
                    fontFamily = NoorLotus,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(files, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (item.isDownloaded) "✓" else if (item.isDownloading) "⟳" else "○",
                            fontFamily = FontAwesome,
                            fontSize = 14.sp,
                            color = headerColor,
                            modifier = Modifier.width(20.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            item.displayName,
                            fontFamily = NoorLotus,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}