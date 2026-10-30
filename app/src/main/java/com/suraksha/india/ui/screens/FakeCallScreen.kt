package com.suraksha.india.ui.screens

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.suraksha.india.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun FakeCallScreen(navController: NavController) {
    val context = LocalContext.current
    var hasAnswered by remember { mutableStateOf(false) }
    var callSeconds by remember { mutableIntStateOf(0) }
    val callerName by remember { mutableStateOf("Family Member (Demo)") }

    DisposableEffect(Unit) {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val ringtone: Ringtone? = RingtoneManager.getRingtone(context, ringtoneUri)
        ringtone?.play()

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 1000, 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, 0)
        }

        onDispose {
            ringtone?.stop()
            vibrator.cancel()
        }
    }

    LaunchedEffect(hasAnswered) {
        if (hasAnswered) {
            while (true) {
                delay(1000)
                callSeconds++
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 60.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = NavySurface,
                shape = CircleShape
            ) {
                Text(
                    text = "SIMULATED CALL DEMO",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .background(Color(0xFF263248), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Caller",
                        tint = PureWhite,
                        modifier = Modifier.size(52.dp)
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(callerName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (hasAnswered) {
                        "Connected: 00:${callSeconds.toString().padStart(2, '0')}"
                    } else {
                        "Incoming Call..."
                    },
                    fontSize = 14.sp,
                    color = if (hasAnswered) EmeraldSafe else TextMuted
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFFD32F2F), shape = CircleShape)
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Reject", tint = PureWhite, modifier = Modifier.size(34.dp))
                }

                if (!hasAnswered) {
                    IconButton(
                        onClick = { hasAnswered = true },
                        modifier = Modifier
                            .size(72.dp)
                            .background(EmeraldSafe, shape = CircleShape)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Accept", tint = PureWhite, modifier = Modifier.size(34.dp))
                    }
                }
            }
        }
    }
}
