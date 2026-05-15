package com.practicum.playlistmaker.data.repository

import android.net.Uri
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.PlaylistEntity
import com.practicum.playlistmaker.data.file.PlaylistCoverManager
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PlaylistsRepositoryImpl(
    database: AppDatabase,
    private val coverManager: PlaylistCoverManager
) : PlaylistsRepository {

    private val playlistDao = database.playlistDao()
    private val trackDao = database.trackDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return fetchPlaylistData(playlistId)
    }

    private fun fetchPlaylistData(playlistIdentifier: Long): Flow<Playlist?> {
        return combine(
            playlistDao.getPlaylist(playlistIdentifier),
            trackDao.getTracksByPlaylistId(playlistIdentifier)
        ) { playlistEntity, trackEntities ->
            playlistEntity?.toDomain(trackEntities.map { it.toDomain() })
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return retrieveAllPlaylistsData()
    }

    private fun retrieveAllPlaylistsData(): Flow<List<Playlist>> {
        return combine(
            playlistDao.getAllPlaylists(),
            trackDao.getAllTracks()
        ) { playlistEntities, allTrackEntities ->
            playlistEntities.map { playlistEntity ->
                val playlistTracks = allTrackEntities.filter { it.playlistId == playlistEntity.id }
                playlistEntity.toDomain(playlistTracks.map { it.toDomain() })
            }
        }
    }

    override suspend fun addNewPlaylist(name: String, description: String, coverImageUri: String?) {
        insertNewPlaylistItem(name, description, coverImageUri)
    }

    private suspend fun insertNewPlaylistItem(playlistName: String, playlistDescription: String, imageCoverUri: String?) {
        // Сохраняем обложку во внутреннее хранилище и получаем локальный путь
        val localCoverPath = if (imageCoverUri != null) {
            try {
                coverManager.saveCoverImage(Uri.parse(imageCoverUri))
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = playlistName,
                description = playlistDescription,
                coverImageUri = localCoverPath ?: imageCoverUri
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        removePlaylist(id)
    }

    private suspend fun removePlaylist(playlistId: Long) {
        // Получаем плейлист перед удалением чтобы удалить его обложку
        val playlist = playlistDao.getPlaylistSync(playlistId)
        
        // Удаляем обложку из локального хранилища если она была
        if (playlist != null && !playlist.coverImageUri.isNullOrEmpty()) {
            if (coverManager.isLocalPath(playlist.coverImageUri)) {
                coverManager.deleteCoverImage(playlist.coverImageUri)
            }
        }
        
        trackDao.removeTracksFromPlaylist(playlistId)
        playlistDao.deletePlaylistById(playlistId)
    }
}

private fun PlaylistEntity.toDomain(tracks: List<com.practicum.playlistmaker.domain.models.Track> = emptyList()): Playlist {
    return Playlist(
        id = id,
        name = name,
        description = description,
        coverImageUri = coverImageUri,
        tracks = tracks
    )
}
