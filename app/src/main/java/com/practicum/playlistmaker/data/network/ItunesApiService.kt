package com.practicum.playlistmaker.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search")
    fun search(
        @Query("term") term: String,
        @Query("limit") limit: Int = 200,
        @Query("media") media: String = "music"
    ): Call<ItunesSearchResponseDto>
}

