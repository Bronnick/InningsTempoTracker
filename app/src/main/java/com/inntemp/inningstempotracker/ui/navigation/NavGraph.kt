package com.inntemp.inningstempotracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.inntemp.inningstempotracker.R
import com.inntemp.inningstempotracker.ui.analytics.AnalyticsScreen
import com.inntemp.inningstempotracker.ui.detail.InningsDetailScreen
import com.inntemp.inningstempotracker.ui.edit.EditInningScreen
import com.inntemp.inningstempotracker.ui.home.HomeScreen
import com.inntemp.inningstempotracker.ui.library.InningsLibraryScreen
import com.inntemp.inningstempotracker.ui.match.CreateMatchScreen
import com.inntemp.inningstempotracker.ui.onboarding.OnboardingScreen
import com.inntemp.inningstempotracker.ui.over.OverInputScreen
import com.inntemp.inningstempotracker.ui.preloader.PreloaderScreen
import com.inntemp.inningstempotracker.ui.settings.SettingsScreen
import com.inntemp.inningstempotracker.ui.theme.LocalAppTheme

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomTabScreens.map { it.route }

    Scaffold(
        containerColor = LocalAppTheme.colors.background,
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Preloader.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Preloader.route) {
                PreloaderScreen(navController)
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(navController)
            }
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.Library.route) {
                InningsLibraryScreen(navController)
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen(navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }
            composable(Screen.CreateMatch.route) {
                CreateMatchScreen(navController)
            }
            composable(Screen.OverInput.route) { entry ->
                val matchId = entry.arguments?.getString("matchId")?.toLong() ?: 0L
                OverInputScreen(navController, matchId)
            }
            composable(Screen.InningsDetail.route) { entry ->
                val matchId = entry.arguments?.getString("matchId")?.toLong() ?: 0L
                InningsDetailScreen(navController, matchId)
            }
            composable(Screen.EditInning.route) { entry ->
                val matchId = entry.arguments?.getString("matchId")?.toLong() ?: 0L
                val overId = entry.arguments?.getString("overId")?.toLong() ?: 0L
                EditInningScreen(navController, matchId, overId)
            }
        }
    }
}

@Composable
private fun AppBottomBar(navController: NavController, currentRoute: String?) {
    val colors = LocalAppTheme.colors

    data class TabItem(val screen: Screen, val icon: androidx.compose.ui.graphics.vector.ImageVector, val labelRes: Int)

    val tabs = listOf(
        TabItem(Screen.Home, Icons.Filled.Home, R.string.tab_home),
        TabItem(Screen.Library, Icons.Filled.LibraryBooks, R.string.tab_library),
        TabItem(Screen.Analytics, Icons.Filled.Analytics, R.string.tab_analytics),
        TabItem(Screen.Settings, Icons.Filled.Settings, R.string.tab_settings),
    )

    NavigationBar(containerColor = colors.card, tonalElevation = 2.dp) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(tab.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.labelRes),
                        tint = if (selected) colors.iconActive else colors.iconInactive
                    )
                },
                label = {
                    Text(
                        text = stringResource(tab.labelRes),
                        style = LocalAppTheme.typography.caption,
                        color = if (selected) colors.iconActive else colors.iconInactive
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = colors.inputBackground)
            )
        }
    }
}
