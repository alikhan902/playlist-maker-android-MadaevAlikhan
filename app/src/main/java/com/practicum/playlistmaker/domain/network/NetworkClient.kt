package com.practicum.playlistmaker.domain.network

import com.practicum.playlistmaker.data.network.BaseResponse

interface NetworkClient {
    fun doRequest(dto: Any): BaseResponse
}
