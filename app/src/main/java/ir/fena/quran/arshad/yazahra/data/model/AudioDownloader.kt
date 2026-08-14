package ir.fena.quran.arshad.yazahra.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object AudioDownloader {
    private const val HTTP_BASE = "http://quran.1fe.ir"
    private const val HTTPS_BASE = "https://quran.1fe.ir"

    suspend fun downloadAyah(
        qari: String,
        surahId: Int,
        ayahNumber: Int,
        context: Context
    ): Boolean = withContext(Dispatchers.IO) {
        val folder = File(context.filesDir, "audio/$qari/${surahId.toString().padStart(3, '0')}")
        if (!folder.exists()) folder.mkdirs()

        val fileName = "${surahId.toString().padStart(3, '0')}${ayahNumber.toString().padStart(3, '0')}.mp3"
        val file = File(folder, fileName)
        if (file.exists()) return@withContext true

        val urls = listOf(
            "$HTTP_BASE/$qari/${surahId.toString().padStart(3, '0')}/$fileName",
            "$HTTPS_BASE/$qari/${surahId.toString().padStart(3, '0')}/$fileName"
        )

        for (urlStr in urls) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(urlStr)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.setRequestProperty("User-Agent", "YaZahraApp")
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedInputStream(connection.inputStream).use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    return@withContext true
                }
            } catch (_: Exception) {
            } finally {
                connection?.disconnect()
            }
        }
        false
    }
}