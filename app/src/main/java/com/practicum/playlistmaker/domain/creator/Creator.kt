package com.practicum.playlistmaker.domain.creator

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.preferences.SearchHistoryPreferences
import com.practicum.playlistmaker.data.preferences.ThemePreferences
import com.practicum.playlistmaker.data.repository.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.repository.TracksRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

object Creator {
    private var database: AppDatabase? = null
    private var searchHistoryPreferences: SearchHistoryPreferences? = null
    private var themePreferences: ThemePreferences? = null

    fun initialize(context: Context) {
        database = AppDatabase.getInstance(context)
        searchHistoryPreferences = SearchHistoryPreferences(context.dataStore)
        themePreferences = ThemePreferences(context.dataStore)
    }

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(), getDatabase())
    }

    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(getDatabase())
    }

    fun getSearchHistoryPreferences(): SearchHistoryPreferences {
        return searchHistoryPreferences ?: throw IllegalStateException("Creator not initialized")
    }

    private fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException("Creator not initialized")
    }

    fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getSearchHistoryPreferences())
    }

    fun getThemePreferences(): ThemePreferences {
        return themePreferences ?: throw IllegalStateException("Creator not initialized")
    }
}
