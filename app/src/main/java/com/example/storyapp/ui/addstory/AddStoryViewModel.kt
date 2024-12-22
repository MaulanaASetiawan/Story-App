package com.example.storyapp.ui.addstory

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private var _currentImgUri = MutableLiveData<Uri?>()
    val currentImageUri: MutableLiveData<Uri?> = _currentImgUri

    fun uploadStory(imageFile: File, desc: String, lat: Double, lon: Double) = repository.uploadStory(imageFile, desc, lat, lon)

    fun setCurrentImgUri(uri: Uri?) {
        _currentImgUri.value = uri
    }
}