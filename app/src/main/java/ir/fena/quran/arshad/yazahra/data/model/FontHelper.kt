package ir.fena.quran.arshad.yazahra.data

import android.content.Context
import ir.fena.quran.arshad.yazahra.R
import java.io.File
import java.io.FileOutputStream

object FontHelper {
    fun copyFontsToFiles(context: Context) {
        val targetDir = File(context.filesDir, "fonts")
        if (!targetDir.exists()) targetDir.mkdirs()

        // لیست فونت‌ها با شناسه منابع
        val fonts = mapOf(
            "noorzar.ttf" to R.font.noorzar,
            "noorlotus.ttf" to R.font.noorlotus,
            "amiri.ttf" to R.font.amiri,
            "scheherazade.ttf" to R.font.scheherazade,
            "taha.ttf" to R.font.taha,
            "fontawesome.ttf" to R.font.fontawesome
        )

        for ((name, resId) in fonts) {
            val targetFile = File(targetDir, name)
            if (!targetFile.exists()) {
                try {
                    context.resources.openRawResource(resId).use { input ->
                        FileOutputStream(targetFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    // خطا در کپی فونت (می‌توانید لاگ بگیرید)
                }
            }
        }
    }
}