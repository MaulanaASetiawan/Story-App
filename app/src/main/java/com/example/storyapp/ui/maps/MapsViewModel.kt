package com.example.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.response.StoriesResponse

class MapsViewModel(repository: StoryRepository): ViewModel() {
    val getStoryWithLocation: LiveData<Result<StoriesResponse>> = repository.getStoryWithLocation()
}