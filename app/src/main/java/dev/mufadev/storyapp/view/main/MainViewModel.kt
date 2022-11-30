package dev.mufadev.storyapp.view.main

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.mufadev.storyapp.di.Injection
import dev.mufadev.storyapp.data.local.entity.Story
import dev.mufadev.storyapp.data.repo.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStories(token: String): LiveData<PagingData<Story>> =
        storyRepository.getStoryPaging(token).cachedIn(viewModelScope)

    fun uploadStory(token: String, imageMultipart: MultipartBody.Part, desc: RequestBody,
        lat: RequestBody?, lon: RequestBody?) =
        storyRepository.uploadStory(token, imageMultipart, desc, lat, lon)
}

class MainViewModelFactory (private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideStoryRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}