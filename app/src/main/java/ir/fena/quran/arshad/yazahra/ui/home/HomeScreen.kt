package ir.fena.quran.arshad.yazahra.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.fena.quran.arshad.yazahra.ui.theme.*

object IconsFA {
    const val BOOK = "\uF02D"
    const val LIST = "\uF0CB"
    const val FILE_ALT = "\uF15C"
    const val HISTORY = "\uF1DA"
    const val HEART = "\uF004"
    const val STAR = "\uF005"
    const val INFO_CIRCLE = "\uF05A"
    const val ENVELOPE = "\uF0E0"
    const val DOWNLOAD = "\uF019"
    const val PLAY = "\uF04B"
    const val MOON = "\uF186"
    const val SUN = "\uF185"
}

@Composable
fun HomeScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onSurahClick: () -> Unit = {},
    onJuzClick: () -> Unit = {},
    onPageClick: () -> Unit = {},
    onContinueReading: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onEstekhareClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onContactClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // سربرگ
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "یا زهرا",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "قرآن کریم",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = if (isDarkMode) IconsFA.SUN else IconsFA.MOON,
                fontFamily = FontAwesome,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(4.dp))   // کم گرد
                    .clickable(onClick = onToggleTheme)
                    .padding(8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.secondary,
            thickness = 2.dp,
            modifier = Modifier.padding(horizontal = 60.dp)
        )
        Spacer(Modifier.height(28.dp))

        // بسم‌الله
        Card(
            shape = RoundedCornerShape(4.dp),   // کم گرد
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontFamily = Scheherazade,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "به نام خداوند بخشنده مهربان",
                        fontFamily = NoorLotus,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ادامه قرائت
        Card(
            shape = RoundedCornerShape(4.dp),   // کم گرد
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onContinueReading)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        IconsFA.PLAY,
                        fontFamily = FontAwesome,
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "ادامه قرائت",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // شروع قرائت
        Text(
            text = "شروع قرائت",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(icon = IconsFA.BOOK, title = "انتخاب سوره", Modifier.weight(1f), onClick = onSurahClick)
            FeatureCard(icon = IconsFA.LIST, title = "انتخاب جزء", Modifier.weight(1f), onClick = onJuzClick)
            FeatureCard(icon = IconsFA.FILE_ALT, title = "انتخاب صفحه", Modifier.weight(1f), onClick = onPageClick)
        }

        Spacer(Modifier.height(28.dp))

        // امکانات
        Text(
            text = "امکانات",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // ردیف سه‌تایی
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(icon = IconsFA.HISTORY, title = "تاریخچه", Modifier.weight(1f), onClick = onHistoryClick)
            FeatureCard(icon = IconsFA.STAR, title = "استخاره", Modifier.weight(1f), onClick = onEstekhareClick)
            FeatureCard(icon = IconsFA.HEART, title = "علاقه‌مندی‌ها", Modifier.weight(1f), onClick = onFavoritesClick)
        }

        Spacer(Modifier.height(12.dp))

        // دانلود به صورت تمام‌عرض (به اندازه سه کارت)
        FeatureCard(
            icon = IconsFA.DOWNLOAD,
            title = "دانلود",
            modifier = Modifier.fillMaxWidth(),  // عرض برابر سه کارت با فاصله‌ها
            onClick = onDownloadClick
        )

        Spacer(Modifier.height(36.dp))

        // دکمه‌های پایین
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onAboutClick) {
                Text(IconsFA.INFO_CIRCLE, fontFamily = FontAwesome, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text("درباره ما", textAlign = TextAlign.Center)
            }
            Spacer(Modifier.width(24.dp))
            TextButton(onClick = onContactClick) {
                Text(IconsFA.ENVELOPE, fontFamily = FontAwesome, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text("ارتباط با ما", textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = "نسخه ۱.۰.۰",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FeatureCard(
    icon: String,
    title: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(4.dp),   // کم گرد
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .height(115.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                icon,
                fontFamily = FontAwesome,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}