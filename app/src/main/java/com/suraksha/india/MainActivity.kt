package com.suraksha.india

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.suraksha.india.ui.navigation.Screen
import com.suraksha.india.ui.screens.*
import com.suraksha.india.ui.theme.SurakshaIndiaTheme
import com.suraksha.india.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurakshaIndiaTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(navController = navController)
                    }
                    composable(Screen.Dashboard.route) {
                        DashboardScreen(navController = navController, viewModel = viewModel)
                    }
                    composable(Screen.Contacts.route) {
                        ContactsScreen(navController = navController, viewModel = viewModel)
                    }
                    composable(Screen.Helplines.route) {
                        HelplinesScreen(navController = navController, viewModel = viewModel)
                    }
                    composable(Screen.Guides.route) {
                        SafetyGuidesScreen(navController = navController, viewModel = viewModel)
                    }
                    composable(Screen.FakeCall.route) {
                        FakeCallScreen(navController = navController)
                    }
                }
            }
        }
    }
}
