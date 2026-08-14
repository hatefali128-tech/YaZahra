package ir.fena.quran.arshad.yazahra.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ReadingHistoryEntry(
    val surahId: Int,
    val surahName: String,
    val ayahNumber: Int,
    val pageId: Int,
    val timestamp: Long
)

object ReadingHistoryManager {
    private const val PREFS_NAME = "quran_settings"
    private const val KEY_HISTORY = "reading_history"

    fun loadHistory(context: Context): List<ReadingHistoryEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<ReadingHistoryEntry>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ReadingHistoryEntry(
                        surahId = obj.getInt("surahId"),
                        surahName = obj.optString("surahName", ""),
                        ayahNumber = obj.getInt("ayahNumber"),
                        pageId = obj.getInt("pageId"),
                        timestamp = obj.getLong("timestamp")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addHistoryEntry(
        context: Context,
        surahId: Int,
        surahName: String,
        ayahNumber: Int,
        pageId: Int
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val oldList = loadHistory(context).toMutableList()

        // حذف ورودی تکراری (همان سوره و آیه)
        oldList.removeAll { it.surahId == surahId && it.ayahNumber == ayahNumber }

        // افزودن ورودی جدید در ابتدای لیست
        oldList.add(
            0,
            ReadingHistoryEntry(
                surahId = surahId,
                surahName = surahName,
                ayahNumber = ayahNumber,
                pageId = pageId,
                timestamp = System.currentTimeMillis()
            )
        )

        // نگهداری حداکثر ۲۰ آیتم آخر
        val limited = oldList.take(20)

        val jsonArray = JSONArray()
        limited.forEach { entry ->
            jsonArray.put(
                JSONObject().apply {
                    put("surahId", entry.surahId)
                    put("surahName", entry.surahName)
                    put("ayahNumber", entry.ayahNumber)
                    put("pageId", entry.pageId)
                    put("timestamp", entry.timestamp)
                }
            )
        }

        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }
}