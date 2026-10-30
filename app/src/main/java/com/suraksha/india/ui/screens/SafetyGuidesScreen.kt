package com.suraksha.india.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.suraksha.india.ui.theme.*
import com.suraksha.india.viewmodel.MainViewModel

@Composable
fun SafetyGuidesScreen(navController: NavController, viewModel: MainViewModel) {
    val guides = viewModel.safetyGuides

    Scaffold(containerColor = NavyBackground) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Offline Safety Handbook", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PureWhite)
            Text("Guidelines and legal protections stored offline on your device.", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(guides) { guide ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(guide.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AccentRed)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(guide.summary, fontSize = 12.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(10.dp))
                            guide.steps.forEach { step ->
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("• ", color = TextMuted, fontWeight = FontWeight.Bold)
                                    Text(step, fontSize = 13.sp, color = PureWhite)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
