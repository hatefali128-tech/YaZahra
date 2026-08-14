package ir.fena.quran.arshad.yazahra.ui.about

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.fena.quran.arshad.yazahra.ui.theme.FontAwesome
import ir.fena.quran.arshad.yazahra.ui.theme.NoorLotus
import ir.fena.quran.arshad.yazahra.ui.theme.NoorZar

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val projects = listOf(
        Triple("ذکر", "https://zekr.app", "پروژه ذکر"),
        Triple("قرآن دات کام", "https://quran.com", "پایگاه بین‌المللی قرآن"),
        Triple("تفسیر نمونه", "https://tafsir.net", "پایگاه تفسیر نمونه"),
        Triple("پایگاه مکارم شیرازی", "https://makarem.ir", "دفتر آیت‌الله مکارم شیرازی"),
        Triple("ملک فهد", "https://qurancomplex.gov.sa", "مجتمع چاپ قرآن ملک فهد"),
        Triple("شهرزاد", "https://shahrezad.app", "پروژه شهرزاد"),
        Triple("امیری", "https://amiri-font.org", "فونت امیری"),
        Triple("نور سافت", "https://noorsoft.org", "مرکز تحقیقات کامپیوتری علوم اسلامی")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // سربرگ با عنوان دقیقاً وسط
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("\uF060", fontFamily = FontAwesome, fontSize = 20.sp)
                Spacer(Modifier.width(4.dp))
                Text("بازگشت", fontFamily = NoorLotus, textAlign = TextAlign.Center)
            }
            Text(
                "درباره ما",
                fontFamily = NoorZar,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(20.dp))

        // کارت اصلی اطلاعات
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🌟", fontSize = 40.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "گروه نرم افزاری ارشد",
                    fontFamily = NoorZar,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "ارائه دهنده اپلیکیشن یا زهرا - قرآن کریم",
                    fontFamily = NoorLotus,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "🌐 www.arshadcms.ir",
                    fontFamily = NoorLotus,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri("https://www.arshadcms.ir") }
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "📧 hosein7hosein@gmail.com",
                    fontFamily = NoorLotus,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { uriHandler.openUri("mailto:hosein7hosein@gmail.com") }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // بخش تشکر
        Text(
            "🙏 تشکر ویژه از پروژه‌های قرآنی",
            fontFamily = NoorZar,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            projects.forEach { (name, url, desc) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { uriHandler.openUri(url) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "🔗 $name",
                            fontFamily = NoorLotus,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            desc,
                            fontFamily = NoorLotus,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "این نرم‌افزار با هدف خدمت به ساحت مقدس قرآن کریم و تسهیل قرائت، تدبر و استخاره طراحی شده است.",
            fontFamily = NoorLotus,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}