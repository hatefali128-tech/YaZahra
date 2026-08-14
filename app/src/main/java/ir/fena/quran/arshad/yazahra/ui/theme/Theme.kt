// Theme.kt
package ir.fena.quran.arshad.yazahra.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import ir.fena.quran.arshad.yazahra.R

// رنگ‌ها
val GreenDark = Color(0xFF1B5E20)
val GreenLight = Color(0xFF2E7D32)
val Gold = Color(0xFFD4AF37)
val Cream = Color(0xFFFFF8E1)
val DarkBg = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceVariant = Color(0xFF2C2C2C)

// فونت‌ها
val NoorZar = FontFamily(Font(R.font.noorzar))
val NoorLotus = FontFamily(Font(R.font.noorlotus))
val Amiri = FontFamily(Font(R.font.amiri))
val Scheherazade = FontFamily(Font(R.font.scheherazade))
val Taha = FontFamily(Font(R.font.taha))
val FontAwesome = FontFamily(Font(R.font.fontawesome))

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = NoorZar, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = NoorZar, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleLarge = TextStyle(fontFamily = Taha, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    bodyLarge = TextStyle(fontFamily = NoorLotus, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = NoorLotus, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = NoorZar, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    labelMedium = TextStyle(fontFamily = NoorLotus, fontSize = 12.sp)
)

private val LightColors = lightColorScheme(
    primary = GreenDark, onPrimary = Color.White,
    primaryContainer = GreenLight, onPrimaryContainer = Color.White,
    secondary = Gold, onSecondary = Color.Black,
    background = Color(0xFFFAFAFA), onBackground = Color.Black,
    surface = Color.White, onSurface = Color.Black,
    surfaceVariant = Cream, onSurfaceVariant = Color(0xFF757575),
    outline = GreenDark.copy(alpha = 0.3f)
)

private val DarkColors = darkColorScheme(
    primary = GreenLight, onPrimary = Color.White,
    primaryContainer = GreenDark, onPrimaryContainer = Color.White,
    secondary = Gold, onSecondary = Color.Black,
    background = DarkBg, onBackground = Color.White,
    surface = DarkSurface, onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant, onSurfaceVariant = Color.LightGray,
    outline = GreenLight.copy(alpha = 0.4f)
)

@Composable
fun YaZahra_Theme(
    darkTheme: Boolean,     // اجباری – از بیرون تعیین می‌شود
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography,
            content = content
        )
    }
}