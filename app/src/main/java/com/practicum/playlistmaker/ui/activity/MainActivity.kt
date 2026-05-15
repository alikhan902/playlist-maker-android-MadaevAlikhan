package com.practicum.playlistmaker.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.practicum.playlistmaker.domain.creator.Creator
import com.practicum.playlistmaker.ui.navigation.PlaylistHost
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Creator.initialize(this)

        setContent {
            val darkTheme = Creator.getThemePreferences().getDarkThemeEnabled().collectAsState(initial = false)
            
            PlaylistMakerTheme(darkTheme = darkTheme.value) {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = AppColors.primaryBlue
                ) {
                    val navController = rememberNavController()
                    PlaylistHost(navController = navController)
                }
            }
        }
    }
}
