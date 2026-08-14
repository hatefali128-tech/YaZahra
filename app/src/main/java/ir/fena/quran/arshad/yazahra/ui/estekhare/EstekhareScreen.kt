package ir.fena.quran.arshad.yazahra.ui.estekhare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import ir.fena.quran.arshad.yazahra.data.DatabaseHelper
import ir.fena.quran.arshad.yazahra.data.model.Verse
import ir.fena.quran.arshad.yazahra.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EstekhareScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val db = remember { DatabaseHelper.getInstance(context) }
    var verse by remember { mutableStateOf<Verse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    fun loadRandomVerse() {
        coroutineScope.launch {
            isLoading = true
            verse = withContext(Dispatchers.IO) { db.getRandomVerse() }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadRandomVerse() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
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
                "استخاره با قرآن",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(16.dp))

        // توضیح صحیح بر اساس قرآن و حدیث
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "استخاره با قرآن، در فرهنگ شیعه، راهی برای طلب خیر از خداوند از طریق تفأل به آیات قرآن کریم است. " +
                            "این عمل ریشه در سیره معصومان (علیهم السلام) دارد و در روایات معتبر به آن اشاره شده است.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Justify
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "قرآن کریم می‌فرماید: «و اگر در کاری اختلاف کردید، آن را به خدا و پیامبر بازگردانید» (نساء/۵۹) " +
                            "و نیز «و در کارها با آنان مشورت کن» (آل‌عمران/۱۵۹). استخاره نوعی بازگشت به خداوند و طلب خیر از اوست.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Justify
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "امام صادق (علیه السلام) فرمودند: «هرگاه مؤمن پس از مشورت و تفکر، همچنان در انجام کاری مردد بود، " +
                            "می‌تواند با قرآن استخاره کند و به آنچه آمد راضی باشد.»",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Justify
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            verse?.let { v ->
                val surahName = allSurahs.find { it.number == v.parentId }?.persianName ?: ""
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            v.title,
                            fontFamily = Scheherazade,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            v.comment,
                            fontFamily = NoorLotus,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "سوره $surahName - آیه ${v.categoryId}",
                            fontFamily = NoorLotus,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "استخاره نباید تنها دلیل انجام یا ترک کار باشد؛ بلکه مؤمن ابتدا تحقیق، مشورت و عقل خود را به کار می‌گیرد، " +
                            "سپس با توکل بر خداوند و بسم الله الرحمن الرحیم، از قرآن طلب خیر می‌کند.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { loadRandomVerse() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("آیه جدید", fontSize = 18.sp)
        }
    }
}