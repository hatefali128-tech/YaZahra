package ir.fena.quran.arshad.yazahra.ui.juz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.fena.quran.arshad.yazahra.data.DatabaseHelper
import ir.fena.quran.arshad.yazahra.ui.theme.FontAwesome
import ir.fena.quran.arshad.yazahra.ui.theme.NoorLotus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun JuzSelectionScreen(
    onJuzSelected: (surahId: Int, ayahNumber: Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { DatabaseHelper.getInstance(context) }
    var juzStartList by remember { mutableStateOf<List<Triple<Int, Int, Int>>>(emptyList()) }
    var manualJuz by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        juzStartList = withContext(Dispatchers.IO) { db.getJuzStartList() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // سربرگ
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("\uF060", fontFamily = FontAwesome, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("بازگشت")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "انتخاب جزء",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ورودی دستی جزء
        OutlinedTextField(
            value = manualJuz,
            onValueChange = {
                manualJuz = it
                showError = false
            },
            label = { Text("شماره جزء") },
            placeholder = { Text("مثلاً ۱۵") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showError,
            supportingText = {
                if (showError) Text("عدد باید بین ۱ تا ۳۰ باشد")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val num = manualJuz.toIntOrNull()
                if (num != null && num in 1..30) {
                    val info = juzStartList.find { it.first == num }
                    if (info != null) {
                        onJuzSelected(info.second, info.third)
                    } else {
                        showError = true
                    }
                } else {
                    showError = true
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("رفتن به جزء", textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // عنوان شبکه
        Text(
            "لیست اجزاء",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // شبکه ۵ ستونه اجزاء
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(juzStartList) { (juz, _, _) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable {
                            val info = juzStartList.find { it.first == juz }
                            if (info != null) onJuzSelected(info.second, info.third)
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "جزء $juz",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}