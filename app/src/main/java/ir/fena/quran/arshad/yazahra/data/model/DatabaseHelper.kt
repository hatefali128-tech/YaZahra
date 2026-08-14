package ir.fena.quran.arshad.yazahra.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ir.fena.quran.arshad.yazahra.data.model.Verse
import java.io.File
import java.io.FileOutputStream

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        private const val DATABASE_NAME = "TafsirNemooneh61.db"
        private const val DB_PATH = "/data/data/ir.fena.quran.arshad.yazahra/databases/"
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    val quranTableName = "Quran"

    init {
        copyDatabaseIfNeeded(context)
    }

    private fun copyDatabaseIfNeeded(context: Context) {
        val dbFile = File(DB_PATH + DATABASE_NAME)
        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()
            context.assets.open("databases/$DATABASE_NAME").use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {}
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun getPageIdForAyah(parentId: Int, categoryId: Int): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT PageID FROM $quranTableName WHERE ParentID=? AND CategoryID=?",
            arrayOf(parentId.toString(), categoryId.toString())
        )
        val pageId = if (cursor.moveToFirst()) cursor.getInt(0) else 1
        cursor.close()
        return pageId
    }

    fun getJuzStartList(): List<Triple<Int, Int, Int>> {
        val result = mutableListOf<Triple<Int, Int, Int>>()
        for (juz in 1..30) {
            val cursor = readableDatabase.rawQuery(
                "SELECT ParentID, CategoryID FROM $quranTableName WHERE Juz=? ORDER BY ParentID, CategoryID LIMIT 1",
                arrayOf(juz.toString())
            )
            if (cursor.moveToFirst()) {
                result.add(Triple(juz, cursor.getInt(0), cursor.getInt(1)))
            }
            cursor.close()
        }
        return result
    }

    fun getPageStart(pageId: Int): Pair<Int, Int>? {
        val cursor = readableDatabase.rawQuery(
            "SELECT ParentID, CategoryID FROM $quranTableName WHERE PageID=? ORDER BY ParentID, CategoryID LIMIT 1",
            arrayOf(pageId.toString())
        )
        val result = if (cursor.moveToFirst()) {
            Pair(cursor.getInt(0), cursor.getInt(1))
        } else null
        cursor.close()
        return result
    }

    fun getTotalPages(): Int {
        val cursor = readableDatabase.rawQuery("SELECT MAX(PageID) FROM $quranTableName", null)
        val total = if (cursor.moveToFirst()) cursor.getInt(0) else 604
        cursor.close()
        return total
    }

    fun getRandomVerse(): Verse? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $quranTableName ORDER BY RANDOM() LIMIT 1",
            null
        )
        val verse = if (cursor.moveToFirst()) {
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
        } else null
        cursor.close()
        return verse
    }

    // 🆕 تغییر وضعیت علاقه‌مندی و برگرداندن وضعیت جدید
    fun toggleFavorite(surahId: Int, categoryId: Int): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT favorite FROM $quranTableName WHERE ParentID=? AND CategoryID=?",
            arrayOf(surahId.toString(), categoryId.toString())
        )
        val current = if (cursor.moveToFirst()) cursor.getInt(0) == 1 else false
        cursor.close()
        val newState = !current
        db.execSQL(
            "UPDATE $quranTableName SET favorite=? WHERE ParentID=? AND CategoryID=?",
            arrayOf(if (newState) 1 else 0, surahId, categoryId)
        )
        return newState
    }

    // 🆕 گرفتن لیست آیات علاقه‌مندی‌شده
    fun getFavoriteVerses(): List<Verse> {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $quranTableName WHERE favorite=1 ORDER BY ParentID, CategoryID",
            null
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
                    favorite = true
                )
            )
        }
        cursor.close()
        return list
    }
}