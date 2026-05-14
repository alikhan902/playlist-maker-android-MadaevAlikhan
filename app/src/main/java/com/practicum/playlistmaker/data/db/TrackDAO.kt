package com.practicum.playlistmaker.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks WHERE trackName = :trackName AND artistName = :artistName")
    fun getTrackByNameAndArtist(trackName: String, artistName: String): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks WHERE favorite = 1")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE playlistId = :playlistId")
    fun getTracksByPlaylistId(playlistId: Long): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("UPDATE tracks SET playlistId = 0 WHERE playlistId = :playlistId")
    suspend fun removeTracksFromPlaylist(playlistId: Long)

    @Query("UPDATE tracks SET favorite = :isFavorite WHERE trackName = :trackName AND artistName = :artistName")
    suspend fun updateFavoriteStatus(trackName: String, artistName: String, isFavorite: Boolean)

    @Query("DELETE FROM tracks WHERE favorite = 0 AND playlistId = 0")
    suspend fun cleanupUnusedTracks()

    @Query("SELECT * FROM tracks")
    fun getAllTracks(): Flow<List<TrackEntity>>
}
