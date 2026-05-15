package com.practicum.playlistmaker.ui.screens.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.creator.Creator
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()
    val favoriteList = tracksRepository.getFavoriteTracks()

    private val _duplicatePlaylistError = MutableStateFlow(false)

    private val _coverImageUri = MutableStateFlow<String?>(null)
    val coverImageUri = _coverImageUri.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.getAllPlaylists().collect { playlistsList ->
                _playlists.value = playlistsList
            }
        }
    }

    // Создание плейлиста с обложкой
    fun createNewPlaylist(playlistTitle: String, playlistDescription: String, imageCoverUri: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingPlaylists = playlistsRepository.getAllPlaylists().first()
            val isDuplicate = existingPlaylists.any { it.name.equals(playlistTitle, ignoreCase = true) }

            if (!isDuplicate) {
                playlistsRepository.addNewPlaylist(playlistTitle, playlistDescription, imageCoverUri)
                _duplicatePlaylistError.value = false
                _coverImageUri.value = null
            } else {
                _duplicatePlaylistError.value = true
            }
        }
    }

    fun setCoverImageUri(imageUri: String?) {
        _coverImageUri.value = imageUri
    }

    fun addTrackToPlaylist(trackItem: Track, playlistIdentifier: Long) {
        viewModelScope.launch {
            tracksRepository.insertSongToPlaylist(trackItem, playlistIdentifier)
        }
    }

    fun toggleFavorite(trackItem: Track, favoriteStatus: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.updateTrackFavoriteStatus(trackItem, favoriteStatus)
        }
    }

    fun saveTrackToDatabase(trackItem: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.saveTrack(trackItem)
        }
    }

    fun deleteSongFromPlaylist(trackItem: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteSongFromPlaylist(trackItem)
        }
    }

    fun updateTrackFavoriteStatus(trackItem: Track, favoriteStatus: Boolean) {
        viewModelScope.launch {
            tracksRepository.updateTrackFavoriteStatus(trackItem, favoriteStatus)
        }
    }

    fun deletePlaylistById(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTracksByPlaylistId(playlistId)
            playlistsRepository.deletePlaylistById(playlistId)
        }
    }

    fun isTrackInPlaylist(trackItem: Track) = tracksRepository.getTrackByNameAndArtist(trackItem)

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistViewModel(
                        Creator.getPlaylistsRepository(),
                        Creator.getTracksRepository()
                    ) as T
                }
            }
    }
}
