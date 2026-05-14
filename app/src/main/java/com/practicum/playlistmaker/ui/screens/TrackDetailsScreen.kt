package com.practicum.playlistmaker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.screens.playlists.PlaylistViewModel
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    track: Track,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PlaylistViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = PlaylistViewModel.getViewModelFactory()
    )

    val context = LocalContext.current

    var currentDisplayedTrack by remember { mutableStateOf(track) }
    var isTrackFavorited by remember { mutableStateOf(currentDisplayedTrack.favorite) }

    var showPlaylistSheet by remember { mutableStateOf(false) }
    var displayPlaylistSelectionSheet by remember { mutableStateOf(false) }

    val trackState by viewModel.isTrackInPlaylist(currentDisplayedTrack).collectAsState(initial = null)
    val playlistList by viewModel.playlists.collectAsState()

    LaunchedEffect(trackState) {
        trackState?.let {
            currentDisplayedTrack = it
            isTrackFavorited = it.favorite
        }
    }

    LaunchedEffect(Unit) {
        val existingTrack = viewModel.isTrackInPlaylist(track).first()
        if (existingTrack == null) {
            viewModel.saveTrackToDatabase(track)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(color = AppColors.white)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Картинка
                    if (currentDisplayedTrack.artworkUrl100.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(currentDisplayedTrack.artworkUrl100)
                                .crossfade(true)
                                .build(),
                            contentDescription = currentDisplayedTrack.trackName,
                            placeholder = painterResource(R.drawable.ic_music),
                            error = painterResource(R.drawable.ic_music),
                            modifier = Modifier
                                .size(312.dp)
                                .padding(bottom = 24.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_music),
                            contentDescription = currentDisplayedTrack.trackName,
                            modifier = Modifier
                                .size(360.dp)
                                .padding(bottom = 24.dp),
                            tint = AppColors.gray
                        )
                    }

                    // Название трека
                    Text(
                        text = currentDisplayedTrack.trackName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Автор
                    Text(
                        text = currentDisplayedTrack.artistName,
                        fontSize = 18.sp,
                        color = AppColors.black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // Иконки
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Плейлист
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(AppColors.lightGray, RoundedCornerShape(50))
                                .clickable { showPlaylistSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                contentDescription = stringResource(R.string.add_to_playlist),
                                tint = AppColors.gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Избранное
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(AppColors.lightGray, RoundedCornerShape(50))
                                .clickable {
                                    val newFavorite = !isTrackFavorited
                                    isTrackFavorited = newFavorite
                                    viewModel.toggleFavorite(currentDisplayedTrack, newFavorite)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isTrackFavorited) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Filled.FavoriteBorder
                                },
                                contentDescription = stringResource(R.string.favorite),
                                tint = if (isTrackFavorited) AppColors.red else AppColors.gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Длительность
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.track_duration),
                            fontSize = 16.sp,
                            color = AppColors.gray
                        )
                        Text(
                            text = currentDisplayedTrack.trackTime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.black
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
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
            }
        }

        // BottomSheet
        // BottomSheet
        if (showPlaylistSheet) {
            PlaylistSelectionBottomSheet(
                playlists = playlistList,
                onPlaylistSelected = { playlist ->
                    viewModel.addTrackToPlaylist(currentDisplayedTrack, playlist.id)
                    showPlaylistSheet = false

                    val message = context.getString(
                        R.string.track_added_to_playlist,
                        currentDisplayedTrack.trackName,
                        playlist.name
                    )

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showPlaylistSheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSelectionBottomSheet(
    playlists: List<com.practicum.playlistmaker.domain.models.Playlist>,
    onPlaylistSelected: (com.practicum.playlistmaker.domain.models.Playlist) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.select_playlist),
                style = MaterialTheme.typography.headlineSmall,
                color = AppColors.black,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )


            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_playlists),
                        color = AppColors.gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistBottomSheetItem(
                            playlist = playlist,
                            onClick = { onPlaylistSelected(playlist) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistBottomSheetItem(
    playlist: com.practicum.playlistmaker.domain.models.Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(AppColors.lightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (playlist.coverImageUri != null) {
                // Показываем обложку плейлиста
                AsyncImage(
                    model = playlist.coverImageUri.toUri(),
                    contentDescription = playlist.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Показываем плейсхолдер
                Icon(
                    painter = painterResource(id = R.drawable.ic_music),
                    contentDescription = null,
                    tint = AppColors.gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                ),
                color = AppColors.black
            )

            Text(
                text = "${playlist.tracks.size} ${stringResource(R.string.tracks_count)}",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackDetailsPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.white) {
            TrackDetailsScreen(
                track = Track("Bohemian Rhapsody", "Queen", "05:55", ""),
                onBackClick = {}
            )
        }
    }
}

