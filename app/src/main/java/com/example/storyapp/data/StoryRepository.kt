package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.local.database.StoryDatabase
import com.example.storyapp.data.local.database.entity.StoryEntity
import com.example.storyapp.data.pref.UserPref
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.NewStoryResponse
import com.example.storyapp.data.remote.response.SignUpResponse
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.helper.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val userPref: UserPref,
    private val database: StoryDatabase,
) {
    fun register(name: String, email: String, password: String): LiveData<Result<SignUpResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        wrapEspressoIdlingResource {
            emit(Result.Loading)
            try {
                val response = apiService.login(email, password)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "An error occurred"))
            }
        }
    }

    suspend fun saveToken(token: String) {
        userPref.saveToken(token)
    }

    fun getStoryList(): LiveData<PagingData<StoryEntity>> = liveData {
        @OptIn(ExperimentalPagingApi::class)
        emitSource(
            Pager(
                config = PagingConfig(pageSize = 5),
                remoteMediator = StoryRemoteMediator(database, apiService, userPref),
                pagingSourceFactory = { database.storyDao().getAllStory() }
            ).liveData
        )
    }

    fun uploadStory(image: File, description: String, lat: Double?, lon: Double?): LiveData<Result<NewStoryResponse>> = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("photo", image.name, requestImageFile)
        val latBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val lonBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

        val token = userPref.getToken().first()

        try {
            val response = apiService.uploadStory("Bearer $token", multipartBody, requestBody, latBody!!, lonBody!!)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    fun getDetailStory(storyId: String): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = userPref.getToken().first()
            val response = apiService.getDetailStory("Bearer $token", storyId)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    fun getStoryWithLocation(): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = userPref.getToken().first()
            val response = apiService.getStoriesWithLocation("Bearer $token", 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    fun getStoriesForWidget(): Result<List<ListStoryItem?>?> = runBlocking {
        try {
            val token = userPref.getToken().first()
            val response = apiService.getStories("Bearer $token")
            Result.Success(response.listStory)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPref: UserPref, database: StoryDatabase) =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPref, database).also { instance = it }
            }
    }
}