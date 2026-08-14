package ir.fena.quran.arshad.yazahra.data

import android.content.Context

object ThemeManager {
    fun getThemeList(context: Context): List<String> {
        return try {
            context.assets.list("themes")?.filter { it.endsWith(".html") }?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun loadThemeContent(context: Context, fileName: String): String {
        return context.assets.open("themes/$fileName").bufferedReader().use { it.readText() }
    }
}