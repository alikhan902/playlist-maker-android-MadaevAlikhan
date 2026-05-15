package com.practicum.playlistmaker.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchHistoryPreferences(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = CoroutineScope(CoroutineName("search-history-preferences") + SupervisorJob())
) {

    private companion object {
        const val MAX_ENTRIES = 10
        const val SEPARATOR = ","
        val HISTORY_KEY = stringPreferencesKey("search_history")
    }

    fun addEntry(word: String) {
        if (word.isEmpty()) {
            return
        }

        coroutineScope.launch {
            dataStore.edit { preferences ->
                val historyString = preferences[HISTORY_KEY].orEmpty()
                val history = if (historyString.isNotEmpty()) {
                    historyString.split(SEPARATOR).toMutableList()
                } else {
                    mutableListOf()
                }

                history.remove(word) // удаляем дубликат если был
                history.add(0, word)

                val subList = history.subList(0, minOf(history.size, MAX_ENTRIES))
                val updatedString = subList.joinToString(SEPARATOR)

                preferences[HISTORY_KEY] = updatedString
            }
        }
    }

    fun getEntries(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            val historyString = preferences[HISTORY_KEY].orEmpty()
            if (historyString.isNotEmpty()) {
                historyString.split(SEPARATOR)
            } else {
                emptyList()
            }
        }
    }
}
