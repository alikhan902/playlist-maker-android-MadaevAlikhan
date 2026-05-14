package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.TrackEntity
import com.practicum.playlistmaker.data.network.TracksSearchRequest
import com.practicum.playlistmaker.data.network.TracksSearchResponse
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.network.NetworkClient
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    database: AppDatabase
) : TracksRepository {

    private val trackDao = database.trackDao()

    override suspend fun searchTracks(expression: String): List<Track> {
        return performNetworkSearch(expression)
    }

    private suspend fun performNetworkSearch(queryExpression: String): List<Track> {
        return withContext(Dispatchers.IO) {
            val networkResponse = networkClient.doRequest(TracksSearchRequest(queryExpression))
            if (networkResponse.resultCode == 200) {
                val tracksData = (networkResponse as TracksSearchResponse).results
                convertDtoToTrackList(tracksData)
            } else {
                throw IOException("Network error: Code ${networkResponse.resultCode}") as Throwable
            }
        }
    }

    private fun convertDtoToTrackList(dtoList: List<com.practicum.playlistmaker.data.network.TrackDto>): List<Track> {
        return dtoList.map { dto ->
            val totalSeconds = dto.trackTimeMillis / 1000
            val minuteValue = totalSeconds / 60
            val secondValue = totalSeconds - minuteValue * 60
            val formattedTime = "%02d:%02d".format(minuteValue, secondValue)
            Track(
                trackName = dto.trackName,
                artistName = dto.artistName,
                trackTime = formattedTime,
                artworkUrl100 = dto.artworkUrl100
            )
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return fetchTrackFromDatabase(track.trackName, track.artistName)
    }

    private fun fetchTrackFromDatabase(musicTitle: String, musicianName: String): Flow<Track?> {
        return trackDao.getTrackByNameAndArtist(musicTitle, musicianName)
            .map { entity -> entity?.toDomain() }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSongToPlaylist(track: Track, playlistId: Long) {
        persistTrackToPlaylist(track, playlistId)
    }

    private suspend fun persistTrackToPlaylist(trackData: Track, playlistIdentifier: Long) {
        withContext(Dispatchers.IO) {
            val trackEntity = TrackEntity(
                trackName = trackData.trackName,
                artistName = trackData.artistName,
                trackTime = trackData.trackTime,
                artworkUrl100 = trackData.artworkUrl100,
                favorite = trackData.favorite,
                playlistId = playlistIdentifier
            )
            trackDao.insertTrack(trackEntity)
        }
    }

    override suspend fun deleteSongFromPlaylist(track: Track) {
        removeTrackFromPlaylist(track)
    }

    private suspend fun removeTrackFromPlaylist(trackToRemove: Track) {
        withContext(Dispatchers.IO) {
            trackDao.insertTrack(
                TrackEntity(
                    trackName = trackToRemove.trackName,
                    artistName = trackToRemove.artistName,
                    trackTime = trackToRemove.trackTime,
                    artworkUrl100 = trackToRemove.artworkUrl100,
                    favorite = trackToRemove.favorite,
                    playlistId = 0L
                )
            )
        }
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        changeFavoriteStatus(track, isFavorite)
    }

    private suspend fun changeFavoriteStatus(trackItem: Track, favoriteFlag: Boolean) {
        withContext(Dispatchers.IO) {
            trackDao.updateFavoriteStatus(trackItem.trackName, trackItem.artistName, favoriteFlag)
        }
    }

    override suspend fun saveTrack(track: Track) {
        persistTrackToDatabase(track)
    }

    private suspend fun persistTrackToDatabase(trackData: Track) {
        withContext(Dispatchers.IO) {
            trackDao.insertTrack(
                TrackEntity(
                    trackName = trackData.trackName,
                    artistName = trackData.artistName,
                    trackTime = trackData.trackTime,
                    artworkUrl100 = trackData.artworkUrl100,
                    favorite = trackData.favorite,
                    playlistId = trackData.playlistId
                )
            )
        }
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        withContext(Dispatchers.IO) {
            trackDao.removeTracksFromPlaylist(playlistId)
        }
    }

    override suspend fun cleanupUnusedTracks() {
        withContext(Dispatchers.IO) {
            trackDao.cleanupUnusedTracks()
        }
    }
}

fun TrackEntity.toDomain(): Track {
    return Track(
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        favorite = favorite,
        playlistId = playlistId
    )
}
