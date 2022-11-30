package dev.mufadev.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dev.mufadev.storyapp.data.network.ApiConfig
import dev.mufadev.storyapp.data.repo.StoryRepository
import dev.mufadev.storyapp.data.local.room.StoryDatabase
import dev.mufadev.storyapp.data.repo.MainRepository

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }

    fun provideUserRepository(context: Context): MainRepository {
        val apiService = ApiConfig.getApiService()
        return MainRepository.getInstance(context.dataStore, apiService)
    }
}