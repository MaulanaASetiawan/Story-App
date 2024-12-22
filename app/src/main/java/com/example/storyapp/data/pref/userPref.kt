package com.example.storyapp.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPref private constructor(private val dataStore: DataStore<Preferences>) {

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_TOKEN]
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
        }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        @Volatile
        private var instance: UserPref? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPref {
            return instance ?: synchronized(this) {
                instance ?: UserPref(dataStore).also { instance = it }
            }
        }
    }
}