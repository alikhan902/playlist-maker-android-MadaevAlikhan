package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.preferences.SearchHistoryPreferences
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepositoryImpl(
    private val searchHistoryPreferences: SearchHistoryPreferences
) : SearchHistoryRepository {

    override fun addSearchQuery(query: String) {
        recordSearchQuery(query)
    }

    private fun recordSearchQuery(searchQuery: String) {
        searchHistoryPreferences.addEntry(searchQuery)
    }

    override fun getSearchHistory(): Flow<List<String>> {
        return retrieveSearchHistory()
    }

    private fun retrieveSearchHistory(): Flow<List<String>> {
        return searchHistoryPreferences.getEntries()
    }
}
