package com.practicum.playlistmaker.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Dark theme colors
val DarkPrimaryBlue = Color(0xFF5A8FFF)
val DarkBackground = Color(0xFF121212)
val DarkSurfaceGray = Color(0xFF1E1E1E)
val DarkText = Color(0xFFFFFFFF)
val DarkSearchBackground = Color(0xFF2C2C2C)

object AppColors {
    val primaryBlue: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalDarkTheme.current) DarkPrimaryBlue else Color(0xFF3777F2)
    
    val white: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalDarkTheme.current) DarkSurfaceGray else Color(0xFFFFFFFF)
    
    val black: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalDarkTheme.current) DarkText else Color(0xFF000000)
    
    val gray: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalDarkTheme.current) Color(0xFFB0B0B0) else Color(0xFF808080)
    
    val lightGray: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalDarkTheme.current) Color(0xFF505050) else Color(0xFFD3D3D3)
    
    val red: Color
        @Composable
        @ReadOnlyComposable
        get() = Color(0xFFFF0000)
    
    val searchBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = if (LocalDarkTheme.current) DarkSearchBackground else Color(0xFFF5F5F5)
}
