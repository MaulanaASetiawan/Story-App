package com.example.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository): ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)

    fun saveToken(token: String) {
        viewModelScope.launch {
            repository.saveToken(token)
        }
    }
}