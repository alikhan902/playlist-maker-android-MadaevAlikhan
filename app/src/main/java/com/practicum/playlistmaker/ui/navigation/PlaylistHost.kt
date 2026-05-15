package com.practicum.playlistmaker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.practicum.playlistmaker.domain.creator.Creator
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.screens.*
import com.practicum.playlistmaker.ui.screens.PlaylistsScreen
import com.practicum.playlistmaker.ui.screens.NewPlaylistScreen
import com.practicum.playlistmaker.ui.screens.TrackDetailsScreen
import com.practicum.playlistmaker.ui.screens.PlaylistScreen

@Composable
fun PlaylistHost(navController: NavHostController) {
    val darkTheme = Creator.getThemePreferences().getDarkThemeEnabled().collectAsState(initial = false)
    
    NavHost(
        navController = navController,
        startDestination = Screens.MAIN.name
    ) {
        composable(Screens.MAIN.name) {
            MainScreen(
                onSearchItemClick = { navigateToSearch(navController) },
                onSettingsItemClick = { navigateToSettings(navController) },
                onPlaylistsItemClick = { navigateToPlaylists(navController) },
                onFavoritesItemClick = { navigateToFavorites(navController) }
            )
        }

        composable(Screens.SEARCH.name) {
            SearchScreen(
                onBackClick = { navigateBack(navController) },
                onTrackClick = { track -> navigateToTrackDetails(navController, track) }
            )
        }

        composable(Screens.SETTINGS.name) {
            SettingsScreen(
                onBackClick = { navigateBack(navController) },
                isDarkTheme = darkTheme.value
            )
        }

        composable(Screens.PLAYLISTS.name) {
            PlaylistsScreen(
                onBackClick = { navigateBack(navController) },
                onAddNewPlaylist = { navigateToNewPlaylist(navController) },
                onPlaylistClick = { playlistId -> navigateToPlaylist(navController, playlistId) }
            )
        }

        composable(
            "${Screens.PLAYLIST_DETAIL.name}/{playlistId}",
            arguments = listOf(navArgument("playlistId") { defaultValue = 0L })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            PlaylistScreen(
                playlistId = playlistId,
                onBackClick = { navigateBack(navController) },
                onTrackClick = { track -> navigateToTrackDetails(navController, track) }
            )
        }

        composable(Screens.NEW_PLAYLIST.name) {
            NewPlaylistScreen(
                onBackClick = { navigateBack(navController) },
                onSaveClick = { navigateBack(navController) }
            )
        }

        composable(Screens.TRACK_DETAILS.name) {
            val track = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Track>("track")
            TrackDetailsScreen(
                track = track ?: Track("", "", "", ""),
                onBackClick = { navigateBack(navController) }
            )
        }

        composable(Screens.FAVORITES.name) {
            FavoritesScreen(
                onBackClick = { navigateBack(navController) },
                onTrackClick = { track -> navigateToTrackDetails(navController, track) }
            )
        }
    }
}

// Навигационные функции
private fun navigateToSearch(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.SEARCH.name)
        navController.navigate(Screens.SEARCH.name) { launchSingleTop = true; restoreState = true }
}

private fun navigateToSettings(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.SETTINGS.name)
        navController.navigate(Screens.SETTINGS.name) { launchSingleTop = true; restoreState = true }
}

private fun navigateToPlaylists(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.PLAYLISTS.name)
        navController.navigate(Screens.PLAYLISTS.name) { launchSingleTop = true; restoreState = true }
}

private fun navigateToPlaylist(navController: NavHostController, playlistId: Long) {
    navController.navigate("${Screens.PLAYLIST_DETAIL.name}/$playlistId") { launchSingleTop = true }
}

private fun navigateToFavorites(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.FAVORITES.name)
        navController.navigate(Screens.FAVORITES.name) { launchSingleTop = true; restoreState = true }
}

private fun navigateToNewPlaylist(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.NEW_PLAYLIST.name)
        navController.navigate(Screens.NEW_PLAYLIST.name) { launchSingleTop = true; restoreState = true }
}

private fun navigateToTrackDetails(navController: NavHostController, track: Track) {
    navController.currentBackStackEntry?.savedStateHandle?.set("track", track)
    navController.navigate(Screens.TRACK_DETAILS.name) { launchSingleTop = true }
}

private fun navigateBack(navController: NavHostController) {
    navController.previousBackStackEntry?.let { navController.popBackStack() }
}

