package dev.mufadev.storyapp.view.maps

import androidx.lifecycle.ViewModel
import dev.mufadev.storyapp.data.repo.MainRepository

class StoryMapsViewModel(private val pref: MainRepository) : ViewModel() {

    fun getStoriesWithMap(token: String) = pref.getStoriesWithMap(token)

}