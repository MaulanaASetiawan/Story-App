package com.example.storyapp

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.pref.UserPref
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.addstory.AddStoryViewModel
import com.example.storyapp.ui.detail.DetailViewModel
import com.example.storyapp.ui.home.HomeViewModel
import com.example.storyapp.ui.login.LoginViewModel
import com.example.storyapp.ui.maps.MapsViewModel
import com.example.storyapp.ui.register.RegisterViewModel


val Context.dataStore by preferencesDataStore(name = "user_preferences")
class ViewModelFactory(private val repository: StoryRepository, preferences: UserPref) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val preferences = UserPref.getInstance(context.dataStore)
                instance ?: ViewModelFactory(Injection.provideRepository(context), preferences)
            }.also { instance = it }
    }
}