package com.practicum.playlistmaker.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.creator.Creator
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    private val queryInputFlow = MutableStateFlow("")
    val searchTextState = queryInputFlow.asStateFlow()

    private val _historyState = MutableStateFlow<List<String>>(emptyList())
    val historyState = _historyState.asStateFlow()

    init {
        // Загрузка истории поиска
        viewModelScope.launch {
            searchHistoryRepository.getSearchHistory().collect { history ->
                _historyState.value = history
            }
        }

        // Выполнение поиска с дебаунс-задержкой
        viewModelScope.launch {
            queryInputFlow
                .debounce(1200)
                .collectLatest { rawQuery ->
                    val trimmedQuery = rawQuery.trim()
                    when {
                        trimmedQuery.isEmpty() -> _searchScreenState.update { SearchState.Initial }
                        else -> {
                            performSearch(trimmedQuery)
                            searchHistoryRepository.addSearchQuery(trimmedQuery)
                        }
                    }
                }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        queryInputFlow.value = newQuery
    }

    fun resetSearch() {
        _searchScreenState.update { SearchState.Initial }
        queryInputFlow.value = ""
    }

    suspend fun executeSearchFromUI(searchQuery: String) = performSearch(searchQuery)

    private suspend fun performSearch(searchQuery: String) {
        _searchScreenState.update { SearchState.Searching }
        try {
            val results = tracksRepository.searchTracks(searchQuery)
            _searchScreenState.update { SearchState.Success(results) }
        } catch (networkError: IOException) {
            _searchScreenState.update { SearchState.Fail("Ошибка сети") }
        } catch (error: Throwable) {
            _searchScreenState.update { SearchState.Fail("Неизвестная ошибка") }
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        Creator.getTracksRepository(),
                        Creator.getSearchHistoryRepository()
                    ) as T
                }
            }
    }
}
