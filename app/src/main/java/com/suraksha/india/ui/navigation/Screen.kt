package com.suraksha.india.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object Contacts : Screen("contacts")
    object Helplines : Screen("helplines")
    object Guides : Screen("guides")
    object FakeCall : Screen("fake_call")
}
