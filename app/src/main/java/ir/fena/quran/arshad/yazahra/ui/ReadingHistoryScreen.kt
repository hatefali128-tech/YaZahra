package ir.fena.quran.arshad.yazahra.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import ir.fena.quran.arshad.yazahra.data.ReadingHistoryEntry
import ir.fena.quran.arshad.yazahra.data.ReadingHistoryManager
import ir.fena.quran.arshad.yazahra.ui.theme.FontAwesome
import ir.fena.quran.arshad.yazahra.ui.theme.NoorLotus
import java.text.SimpleDateFormat
import java.util.*

fun formatTimestamp(timestamp: Long): String {
    val now = Calendar.getInstance()
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeStr = timeFormat.format(Date(timestamp))

    val isToday = now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)

    val isYesterday = now.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR) == 1

    return when {
        isToday -> "امروز ساعت $timeStr"
        isYesterday -> "دیروز ساعت $timeStr"
        else -> {
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val month = when (cal.get(Calendar.MONTH)) {
                0 -> "ژانویه"; 1 -> "فوریه"; 2 -> "مارس"; 3 -> "آوریل"; 4 -> "مه"; 5 -> "ژوئن"
                6 -> "ژوئیه"; 7 -> "اوت"; 8 -> "سپتامبر"; 9 -> "اکتبر"; 10 -> "نوامبر"; 11 -> "دسامبر"
                else -> ""
            }
            val year = cal.get(Calendar.YEAR)
            "$day $month $year ساعت $timeStr"
        }
    }
}

@Composable
fun ReadingHistoryScreen(
    onBack: () -> Unit,
    onItemClick: (surahId: Int, ayahNumber: Int, pageId: Int) -> Unit
) {
    val context = LocalContext.current
    val historyList = remember { mutableStateOf(ReadingHistoryManager.loadHistory(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // سربرگ
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("\uF060", fontFamily = FontAwesome, fontSize = 20.sp)
                Spacer(Modifier.width(4.dp))
                Text("بازگشت")
            }
            Spacer(Modifier.weight(1f))
            Text(
                "تاریخچه قرائت",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.secondary)

        Spacer(Modifier.height(16.dp))

        if (historyList.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "هنوز قرائتی ثبت نشده است",
                    fontFamily = NoorLotus,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(historyList.value) { entry ->
                    HistoryItem(
                        entry = entry,
                        onClick = { onItemClick(entry.surahId, entry.ayahNumber, entry.pageId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(
    entry: ReadingHistoryEntry,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "شروع قرائت از سوره ${entry.surahName} آیه ${entry.ayahNumber}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "مشاهده شده ${formatTimestamp(entry.timestamp)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}