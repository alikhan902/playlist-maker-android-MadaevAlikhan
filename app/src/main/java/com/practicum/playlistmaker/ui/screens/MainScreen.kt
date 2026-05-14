package com.practicum.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.creator.Creator
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import androidx.compose.ui.graphics.Color

@Composable
fun MainScreen(
    onSearchItemClick: () -> Unit,
    onSettingsItemClick: () -> Unit,
    onPlaylistsItemClick: () -> Unit,
    onFavoritesItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(Unit) {
        Creator.getTracksRepository().cleanupUnusedTracks()
    }


    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Белый контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(
                    color = AppColors.white,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            // Контент меню
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                MenuItem(
                    icon = Icons.Default.Search,
                    title = stringResource(R.string.search_menu_item),
                    onClick = onSearchItemClick
                )

                MenuItem(
                    icon = Icons.Default.LibraryMusic,
                    title = stringResource(R.string.playlists_menu_item),
                    onClick = onPlaylistsItemClick

                )

                MenuItem(
                    icon = Icons.Default.FavoriteBorder,
                    title = stringResource(R.string.favorites_menu_item),
                    onClick = onFavoritesItemClick

                )

                MenuItem(
                    icon = Icons.Default.Settings,
                    title = stringResource(R.string.settings_menu_item),
                    onClick = onSettingsItemClick
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Синяя шапка поверх контента
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(AppColors.primaryBlue)
                .windowInsetsPadding(WindowInsets.statusBars),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(R.string.main_screen_title),
                color = Color(0xFFFFFFFF),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 31.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(28.dp),
            tint = AppColors.black
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp
            ),
            color = AppColors.black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Go",
            modifier = Modifier.size(30.dp),
            tint = AppColors.gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.primaryBlue) {
            MainScreen(
                onSearchItemClick = { },
                onSettingsItemClick = { },
                onPlaylistsItemClick = { },
                onFavoritesItemClick = { }
            )
        }
    }
}
