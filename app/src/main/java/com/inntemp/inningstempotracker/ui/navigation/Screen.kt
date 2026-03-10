package com.inntemp.inningstempotracker.ui.navigation

sealed class Screen(val route: String) {
    object Preloader : Screen("preloader")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Library : Screen("library")
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
    object CreateMatch : Screen("create_match")
    object OverInput : Screen("over_input/{matchId}") {
        fun createRoute(matchId: Long) = "over_input/$matchId"
    }
    object InningsDetail : Screen("innings_detail/{matchId}") {
        fun createRoute(matchId: Long) = "innings_detail/$matchId"
    }
    object EditInning : Screen("edit_inning/{matchId}/{overId}") {
        fun createRoute(matchId: Long, overId: Long) = "edit_inning/$matchId/$overId"
    }
}

val bottomTabScreens = listOf(Screen.Home, Screen.Library, Screen.Analytics, Screen.Settings)
