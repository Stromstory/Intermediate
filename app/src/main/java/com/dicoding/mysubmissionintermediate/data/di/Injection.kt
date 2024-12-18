package com.dicoding.mysubmissionintermediate.data.di

import android.content.Context
import com.dicoding.mysubmissionintermediate.data.db.StoryDatabase
import com.dicoding.mysubmissionintermediate.data.preference.UserPref
import com.dicoding.mysubmissionintermediate.data.preference.dataStore
import com.dicoding.mysubmissionintermediate.data.repository.StoryRepository
import com.dicoding.mysubmissionintermediate.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(
        context: Context
    ): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val pref = UserPref.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(pref, apiService, database)
    }
}