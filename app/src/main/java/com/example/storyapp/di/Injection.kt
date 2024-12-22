package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.database.StoryDatabase
import com.example.storyapp.data.pref.UserPref
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.dataStore

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPref = UserPref.getInstance(context.dataStore)
        val database = StoryDatabase.getDb(context)
        return StoryRepository.getInstance(apiService, userPref, database)
    }
}