package com.androidforge.streakhabit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.androidforge.streakhabit.core.ui.theme.StreakHabitTheme
import com.androidforge.streakhabit.navigation.AppNavHost
import com.androidforge.streakhabit.presentation.settings.SettingsUiState
import com.androidforge.streakhabit.presentation.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            // Collect UI state for theme, defaulting to SYSTEM if not yet loaded
            val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            val appTheme = (settingsUiState as? SettingsUiState.Success)?.appTheme
                ?: com.androidforge.streakhabit.domain.model.AppTheme.SYSTEM

            StreakHabitTheme(appTheme = appTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StreakHabitTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // For preview, provide a mock NavController or simplify the content
            val navController = rememberNavController() // This will work for basic preview, but might not handle complex navigation logic
            AppNavHost(navController = navController)
        }
    }
}