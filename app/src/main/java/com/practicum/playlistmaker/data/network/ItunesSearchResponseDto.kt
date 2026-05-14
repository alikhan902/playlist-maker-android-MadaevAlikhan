package com.practicum.playlistmaker.data.network

import com.google.gson.annotations.SerializedName

data class ItunesSearchResponseDto(
    @SerializedName("resultCount")
    val resultCount: Int = 0,
    @SerializedName("results")
    val results: List<ItunesTrackDto> = emptyList()
)

