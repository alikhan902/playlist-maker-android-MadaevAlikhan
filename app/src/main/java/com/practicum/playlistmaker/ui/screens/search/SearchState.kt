package com.practicum.playlistmaker.ui.screens.search

import com.practicum.playlistmaker.domain.models.Track

sealed class SearchState {
    object Initial : SearchState() // Первоначальное состояние
    object Searching : SearchState() // Состояние при начале поиска
    data class Success(val foundList: List<Track>) : SearchState() // Состояние при успешном завершении поиска
    data class Fail(val error: String) : SearchState() // Состояние, если при запросе к серверу произошла ошибка
}
