package ir.fena.quran.arshad.yazahra.data.model

data class Verse(
    val parentId: Int,
    val categoryId: Int,
    val pageId: Int,
    val juz: Int,
    val title: String,
    val comment: String,
    val tafsir: String,
    val pavaragi: String,
    val favorite: Boolean
)

data class Group(
    val headVerse: Verse,
    val memberVerses: List<Verse>  // شامل خود سرگروه
)

fun groupVerses(verses: List<Verse>): List<Group> {
    val groups = mutableListOf<Group>()
    var currentHead: Verse? = null
    val currentMembers = mutableListOf<Verse>()

    for (verse in verses) {
        val tafsir = verse.tafsir.trim()

        // سرگروه: تفسیر واقعی (طول > 10) و نه با #
        val isHead = tafsir.isNotEmpty() && !tafsir.startsWith("#") && tafsir.length > 10

        if (isHead) {
            if (currentHead != null) {
                groups.add(Group(currentHead, currentMembers.toList()))
            }
            currentHead = verse
            currentMembers.clear()
            currentMembers.add(verse)
        } else {
            if (currentHead == null) {
                // اگر سرگروهی وجود نداشت، یک گروه تک‌آیه‌ای بساز
                groups.add(Group(verse, listOf(verse)))
            } else {
                currentMembers.add(verse)
            }
        }
    }

    if (currentHead != null) {
        groups.add(Group(currentHead, currentMembers.toList()))
    }

    return groups
}