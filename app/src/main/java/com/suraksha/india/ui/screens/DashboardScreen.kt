package com.suraksha.india.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.suraksha.india.ui.navigation.Screen
import com.suraksha.india.ui.theme.*
import com.suraksha.india.util.SmsDispatcherHelper
import com.suraksha.india.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val lat by viewModel.latitude.collectAsState()
    val lng by viewModel.longitude.collectAsState()
    val isFetchingLocation by viewModel.isLocationFetching.collectAsState()
    val isStrobeActive by viewModel.isStrobeActive.collectAsState()
    val statusMsg by viewModel.sosStatusMessage.collectAsState()

    var isCountdownRunning by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableIntStateOf(3) }

    val requiredPermissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CALL_PHONE
    )

    var hasRequiredPermissions by remember {
        mutableStateOf(
            requiredPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasRequiredPermissions = results.values.all { it }
        if (hasRequiredPermissions) {
            viewModel.refreshLocation()
        } else {
            Toast.makeText(context, "Permissions are required for SMS & GPS alerts.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(isCountdownRunning) {
        if (isCountdownRunning) {
            countdownSeconds = 3
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds--
            }
            isCountdownRunning = false
            viewModel.dispatchSOSAlert { result ->
                when (result) {
                    is SmsDispatcherHelper.DispatchResult.Submitted -> {
                        Toast.makeText(context, "SOS SMS request submitted. Delivery is not confirmed.", Toast.LENGTH_LONG).show()
                    }
                    is SmsDispatcherHelper.DispatchResult.PartialFailure -> {
                        Toast.makeText(context, "Partial failure sending SMS.", Toast.LENGTH_LONG).show()
                    }
                    is SmsDispatcherHelper.DispatchResult.Failure -> {
                        Toast.makeText(context, result.reason, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Scaffold(
        containerColor = NavyBackground,
        bottomBar = {
            NavigationBar(containerColor = NavySurface) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentRed,
                        selectedTextColor = AccentRed,
                        indicatorColor = NavyCard
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Contacts.route) },
                    icon = { Icon(Icons.Default.Contacts, contentDescription = "Contacts") },
                    label = { Text("Contacts") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Helplines.route) },
                    icon = { Icon(Icons.Default.Call, contentDescription = "Helplines") },
                    label = { Text("Helplines") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Guides.route) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Safety") },
                    label = { Text("Handbook") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("SURAKSHA INDIA", fontSize = 22.sp, fontWeight = FontWeight.Black, color = PureWhite)
                        Text("Emergency Assistance Dashboard", fontSize = 12.sp, color = TextMuted)
                    }
                    IconButton(
                        onClick = {
                            if (!hasRequiredPermissions) {
                                permissionLauncher.launch(requiredPermissions)
                            } else {
                                viewModel.refreshLocation()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh GPS", tint = TextMuted)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "GPS",
                            tint = if (lat != null && lng != null) EmeraldSafe else AmberAlert
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = when {
                                    isFetchingLocation -> "Acquiring GPS fix..."
                                    lat != null && lng != null -> "GPS Location Active"
                                    else -> "GPS Unavailable"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Text(
                                text = if (lat != null && lng != null)
                                    "Lat: ${"%.4f".format(lat)}, Lng: ${"%.4f".format(lng)}"
                                else
                                    "Enable device location for SMS coordinates",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .scale(if (isCountdownRunning) 1f else pulseScale)
                        .border(
                            width = 4.dp,
                            brush = Brush.radialGradient(listOf(CrimsonPulse, Color.Transparent)),
                            shape = CircleShape
                        )
                        .padding(14.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(listOf(CrimsonPulse, AccentRed))
                        )
                        .clickable {
                            if (!hasRequiredPermissions) {
                                permissionLauncher.launch(requiredPermissions)
                            } else {
                                isCountdownRunning = !isCountdownRunning
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isCountdownRunning) {
                            Text(
                                text = "$countdownSeconds",
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Black,
                                color = PureWhite
                            )
                            Text(
                                text = "TAP TO CANCEL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "SOS",
                                tint = PureWhite,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SOS",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = PureWhite
                            )
                            Text(
                                text = "PRESS FOR EMERGENCY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            item {
                if (statusMsg != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = statusMsg ?: "",
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            color = PureWhite
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = { SmsDispatcherHelper.initiatePhoneCall(context, "112") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call 112", tint = AccentRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Direct Dial Emergency 112", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "EMERGENCY TOOLS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Strobe Siren",
                        subtitle = if (isStrobeActive) "Flashing" else "Off",
                        icon = Icons.Default.FlashOn,
                        isActive = isStrobeActive,
                        onClick = { viewModel.toggleStrobe() }
                    )
                    DashboardActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Simulated Call",
                        subtitle = "Discreet Exit",
                        icon = Icons.Default.PhoneCallback,
                        isActive = false,
                        onClick = { navController.navigate(Screen.FakeCall.route) }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DashboardActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) AccentRed.copy(alpha = 0.25f) else NavySurface
        ),
        border = if (isActive) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) AccentRed else PureWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PureWhite)
            Text(subtitle, fontSize = 11.sp, color = TextMuted)
        }
    }
}
