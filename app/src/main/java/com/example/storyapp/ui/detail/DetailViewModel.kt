package com.example.storyapp.ui.detail

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository

class DetailViewModel(private val repository: StoryRepository): ViewModel() {
    fun getDetailStory(storyId: String) = repository.getDetailStory(storyId)
}