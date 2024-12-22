package com.example.storyapp.ui.register

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository

class RegisterViewModel(private val repository: StoryRepository): ViewModel() {
    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}