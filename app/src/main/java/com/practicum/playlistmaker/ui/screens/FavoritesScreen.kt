package com.practicum.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.screens.playlists.PlaylistViewModel
import com.practicum.playlistmaker.ui.screens.search.components.TrackListItem
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PlaylistViewModel = viewModel(
        factory = PlaylistViewModel.getViewModelFactory()
    )

    val favoriteTrackList by viewModel.favoriteList.collectAsState(initial = emptyList())

    var displayDeleteConfirmation by remember { mutableStateOf(false) }
    var currentSelectedTrack by remember { mutableStateOf<Track?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(AppColors.white)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {

                if (favoriteTrackList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.no_tracks),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = null
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.no_favorites),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.black
                            )
                        }
                    }
                }else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(favoriteTrackList) { track ->
                            TrackListItem(
                                track = track,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { onTrackClick(track) },
                                        onLongClick = {
                                            currentSelectedTrack = track
                                            displayDeleteConfirmation = true
                                        }
                                    )
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Верхняя панель
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(AppColors.white),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() },
                    tint = AppColors.black
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.favorites_screen_title),
                    color = AppColors.black,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                )
            }
        }
    }

    // Окно подтверждения удаления
    if (displayDeleteConfirmation && currentSelectedTrack != null) {
        AlertDialog(
            onDismissRequest = { displayDeleteConfirmation = false },
            title = { Text(text = stringResource(R.string.delete_track)) },
            text = { Text(text = stringResource(R.string.delete_track_favorites_confirm)) },
            confirmButton = {
                Text(
                    text = stringResource(R.string.yes),
                    modifier = Modifier
                        .clickable {
                            viewModel.updateTrackFavoriteStatus(currentSelectedTrack!!, false)
                            displayDeleteConfirmation = false
                        }
                        .padding(8.dp)
                )
            },
            dismissButton = {
                Text(
                    text = stringResource(R.string.no),
                    modifier = Modifier
                        .clickable { displayDeleteConfirmation = false }
                        .padding(8.dp)
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.white) {
            FavoritesScreen(
                onBackClick = { },
                onTrackClick = { }
            )
        }
    }
}
