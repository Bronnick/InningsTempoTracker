package com.inntemp.inningstempotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.inntemp.inningstempotracker.data.datastore.AppPreferences
import com.inntemp.inningstempotracker.ui.navigation.AppNavGraph
import com.inntemp.inningstempotracker.ui.theme.InningsTempoTrackerTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferences: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by preferences.themeSelection.collectAsState(initial = "light")
            InningsTempoTrackerTheme(darkTheme = theme == "dark") {
                AppNavGraph()
            }
        }
    }
}
