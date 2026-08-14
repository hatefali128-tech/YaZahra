package ir.fena.quran.arshad.yazahra

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.fena.quran.arshad.yazahra.ui.theme.FontAwesome
import ir.fena.quran.arshad.yazahra.ui.theme.NoorLotus
import ir.fena.quran.arshad.yazahra.ui.theme.Scheherazade

// کلاس داده‌ی سوره (اسم متفاوت برای جلوگیری از تداخل)
data class QuranSurah(
    val number: Int,
    val arabicName: String,
    val persianName: String,
    val ayahCount: Int
)

// لیست کامل ۱۱۴ سوره
val allSurahs = listOf(
    QuranSurah(1, "الفاتحة", "فاتحه", 7),
    QuranSurah(2, "البقرة", "بقره", 286),
    QuranSurah(3, "آل عمران", "آل عمران", 200),
    QuranSurah(4, "النساء", "نساء", 176),
    QuranSurah(5, "المائدة", "مائده", 120),
    QuranSurah(6, "الأنعام", "انعام", 165),
    QuranSurah(7, "الأعراف", "اعراف", 206),
    QuranSurah(8, "الأنفال", "انفال", 75),
    QuranSurah(9, "التوبة", "توبه", 129),
    QuranSurah(10, "يونس", "یونس", 109),
    QuranSurah(11, "هود", "هود", 123),
    QuranSurah(12, "يوسف", "یوسف", 111),
    QuranSurah(13, "الرعد", "رعد", 43),
    QuranSurah(14, "إبراهيم", "ابراهیم", 52),
    QuranSurah(15, "الحجر", "حجر", 99),
    QuranSurah(16, "النحل", "نحل", 128),
    QuranSurah(17, "الإسراء", "اسراء", 111),
    QuranSurah(18, "الكهف", "کهف", 110),
    QuranSurah(19, "مريم", "مریم", 98),
    QuranSurah(20, "طه", "طه", 135),
    QuranSurah(21, "الأنبياء", "انبیاء", 112),
    QuranSurah(22, "الحج", "حج", 78),
    QuranSurah(23, "المؤمنون", "مؤمنون", 118),
    QuranSurah(24, "النور", "نور", 64),
    QuranSurah(25, "الفرقان", "فرقان", 77),
    QuranSurah(26, "الشعراء", "شعراء", 227),
    QuranSurah(27, "النمل", "نمل", 93),
    QuranSurah(28, "القصص", "قصص", 88),
    QuranSurah(29, "العنكبوت", "عنکبوت", 69),
    QuranSurah(30, "الروم", "روم", 60),
    QuranSurah(31, "لقمان", "لقمان", 34),
    QuranSurah(32, "السجدة", "سجده", 30),
    QuranSurah(33, "الأحزاب", "احزاب", 73),
    QuranSurah(34, "سبأ", "سبأ", 54),
    QuranSurah(35, "فاطر", "فاطر", 45),
    QuranSurah(36, "يس", "یس", 83),
    QuranSurah(37, "الصافات", "صافات", 182),
    QuranSurah(38, "ص", "ص", 88),
    QuranSurah(39, "الزمر", "زمر", 75),
    QuranSurah(40, "غافر", "غافر", 85),
    QuranSurah(41, "فصلت", "فصلت", 54),
    QuranSurah(42, "الشورى", "شوری", 53),
    QuranSurah(43, "الزخرف", "زخرف", 89),
    QuranSurah(44, "الدخان", "دخان", 59),
    QuranSurah(45, "الجاثية", "جاثیه", 37),
    QuranSurah(46, "الأحقاف", "احقاف", 35),
    QuranSurah(47, "محمد", "محمد", 38),
    QuranSurah(48, "الفتح", "فتح", 29),
    QuranSurah(49, "الحجرات", "حجرات", 18),
    QuranSurah(50, "ق", "ق", 45),
    QuranSurah(51, "الذاريات", "ذاریات", 60),
    QuranSurah(52, "الطور", "طور", 49),
    QuranSurah(53, "النجم", "نجم", 62),
    QuranSurah(54, "القمر", "قمر", 55),
    QuranSurah(55, "الرحمن", "الرحمن", 78),
    QuranSurah(56, "الواقعة", "واقعه", 96),
    QuranSurah(57, "الحديد", "حدید", 29),
    QuranSurah(58, "المجادلة", "مجادله", 22),
    QuranSurah(59, "الحشر", "حشر", 24),
    QuranSurah(60, "الممتحنة", "ممتحنه", 13),
    QuranSurah(61, "الصف", "صف", 14),
    QuranSurah(62, "الجمعة", "جمعه", 11),
    QuranSurah(63, "المنافقون", "منافقون", 11),
    QuranSurah(64, "التغابن", "تغابن", 18),
    QuranSurah(65, "الطلاق", "طلاق", 12),
    QuranSurah(66, "التحريم", "تحریم", 12),
    QuranSurah(67, "الملك", "ملک", 30),
    QuranSurah(68, "القلم", "قلم", 52),
    QuranSurah(69, "الحاقة", "حاقه", 52),
    QuranSurah(70, "المعارج", "معارج", 44),
    QuranSurah(71, "نوح", "نوح", 28),
    QuranSurah(72, "الجن", "جن", 28),
    QuranSurah(73, "المزمل", "مزمل", 20),
    QuranSurah(74, "المدثر", "مدثر", 56),
    QuranSurah(75, "القيامة", "قیامت", 40),
    QuranSurah(76, "الإنسان", "انسان", 31),
    QuranSurah(77, "المرسلات", "مرسلات", 50),
    QuranSurah(78, "النبأ", "نبأ", 40),
    QuranSurah(79, "النازعات", "نازعات", 46),
    QuranSurah(80, "عبس", "عبس", 42),
    QuranSurah(81, "التكوير", "تکویر", 29),
    QuranSurah(82, "الإنفطار", "انفطار", 19),
    QuranSurah(83, "المطففين", "مطففین", 36),
    QuranSurah(84, "الإنشقاق", "انشقاق", 25),
    QuranSurah(85, "البروج", "بروج", 22),
    QuranSurah(86, "الطارق", "طارق", 17),
    QuranSurah(87, "الأعلى", "اعلی", 19),
    QuranSurah(88, "الغاشية", "غاشیه", 26),
    QuranSurah(89, "الفجر", "فجر", 30),
    QuranSurah(90, "البلد", "بلد", 20),
    QuranSurah(91, "الشمس", "شمس", 15),
    QuranSurah(92, "الليل", "لیل", 21),
    QuranSurah(93, "الضحى", "ضحی", 11),
    QuranSurah(94, "الشرح", "شرح", 8),
    QuranSurah(95, "التين", "تین", 8),
    QuranSurah(96, "العلق", "علق", 19),
    QuranSurah(97, "القدر", "قدر", 5),
    QuranSurah(98, "البينة", "بینه", 8),
    QuranSurah(99, "الزلزلة", "زلزله", 8),
    QuranSurah(100, "العاديات", "عادیات", 11),
    QuranSurah(101, "القارعة", "قارعه", 11),
    QuranSurah(102, "التكاثر", "تکاثر", 8),
    QuranSurah(103, "العصر", "عصر", 3),
    QuranSurah(104, "الهمزة", "همزه", 9),
    QuranSurah(105, "الفيل", "فیل", 5),
    QuranSurah(106, "قريش", "قریش", 4),
    QuranSurah(107, "الماعون", "ماعون", 7),
    QuranSurah(108, "الكوثر", "کوثر", 3),
    QuranSurah(109, "الكافرون", "کافرون", 6),
    QuranSurah(110, "النصر", "نصر", 3),
    QuranSurah(111, "المسد", "مسد", 5),
    QuranSurah(112, "الإخلاص", "اخلاص", 4),
    QuranSurah(113, "الفلق", "فلق", 5),
    QuranSurah(114, "الناس", "ناس", 6)
)

// صفحه انتخاب سوره
@Composable
fun SurahSelectionScreen(
    onSurahSelected: (QuranSurah) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) allSurahs
        else allSurahs.filter {
            it.persianName.contains(searchQuery.trim(), ignoreCase = true) ||
                    it.arabicName.contains(searchQuery.trim(), ignoreCase = true) ||
                    it.number.toString() == searchQuery.trim()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // سربرگ
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("\uF060", fontFamily = FontAwesome, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("بازگشت")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("انتخاب سوره", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // جستجو
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("جستجوی نام یا شماره سوره...", fontFamily = NoorLotus, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // گرید ۴ ستونه
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered) { surah ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { onSurahSelected(surah) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${surah.number}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(surah.persianName, fontFamily = NoorLotus, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center, maxLines = 1)
                    }
                }
            }
        }
    }
}