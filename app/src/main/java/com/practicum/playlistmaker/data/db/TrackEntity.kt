package com.practicum.playlistmaker.data.db

import androidx.room.Entity

@Entity(
    tableName = "tracks",
    primaryKeys = ["trackName", "artistName"]
)
data class TrackEntity(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val favorite: Boolean = false,
    val playlistId: Long = 0
)
