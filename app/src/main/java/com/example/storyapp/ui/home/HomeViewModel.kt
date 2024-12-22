package com.example.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.local.database.entity.StoryEntity

class HomeViewModel(repository: StoryRepository): ViewModel() {
    val stories: LiveData<PagingData<StoryEntity>> =
        repository.getStoryList().cachedIn(viewModelScope)
}