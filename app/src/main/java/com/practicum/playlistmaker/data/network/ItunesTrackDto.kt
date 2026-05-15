package com.practicum.playlistmaker.data.network

import com.google.gson.annotations.SerializedName

data class ItunesTrackDto(
    @SerializedName("trackId") val trackId: Long? = null,
    @SerializedName("trackName") val trackName: String? = null,
    @SerializedName("artistName") val artistName: String? = null,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long? = null,
    @SerializedName("artworkUrl100") val artworkUrl100: String? = null,
    @SerializedName("previewUrl") val previewUrl: String? = null,
    @SerializedName("collectionName") val collectionName: String? = null
)

