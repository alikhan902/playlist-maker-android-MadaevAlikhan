package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.domain.network.NetworkClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {

    private val api: ItunesApiService

    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"
        private const val DEFAULT_LIMIT = 200
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ItunesApiService::class.java)
    }

    override fun doRequest(dto: Any): TracksSearchResponse {
        return executeSearchRequest(dto)
    }

    private fun executeSearchRequest(requestDto: Any): TracksSearchResponse {
        val searchRequest = requestDto as? TracksSearchRequest
            ?: return TracksSearchResponse(emptyList()).apply { resultCode = 400 }

        return performApiCall(searchRequest)
    }

    private fun performApiCall(searchRequest: TracksSearchRequest): TracksSearchResponse {
        val apiCall: Call<ItunesSearchResponseDto> = api.search(term = searchRequest.expression, limit = DEFAULT_LIMIT)

        return try {
            val httpResponse = apiCall.execute()
            if (httpResponse.isSuccessful) {
                val responseBody = httpResponse.body()
                val processedResults = (responseBody?.results ?: emptyList()).mapNotNull { convertToTrackDto(it) }
                TracksSearchResponse(processedResults).apply { resultCode = httpResponse.code() }
            } else {
                TracksSearchResponse(emptyList()).apply { resultCode = httpResponse.code() }
            }
        } catch (_: IOException) {
            // Нетворковая ошибка / таймаут
            TracksSearchResponse(emptyList()).apply { resultCode = 500 }
        } catch (_: Throwable) {
            TracksSearchResponse(emptyList()).apply { resultCode = 500 }
        }
    }


    private fun convertToTrackDto(itunesData: ItunesTrackDto): TrackDto? {
        // Пропускаем треки без имени и артиста
        val trackTitle = itunesData.trackName?.trim()
        val artistTitle = itunesData.artistName?.trim()
        if (trackTitle.isNullOrEmpty() && artistTitle.isNullOrEmpty()) return null

        val durationMillis = itunesData.trackTimeMillis?.toInt() ?: 0
        return TrackDto(
            trackName = trackTitle ?: "",
            artistName = artistTitle ?: "",
            trackTimeMillis = durationMillis,
            artworkUrl100 = itunesData.artworkUrl100 ?: ""
        )
    }
}

