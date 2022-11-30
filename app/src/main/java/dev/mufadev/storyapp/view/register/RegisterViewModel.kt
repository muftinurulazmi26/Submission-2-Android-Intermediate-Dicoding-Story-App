package dev.mufadev.storyapp.view.register

import androidx.lifecycle.*
import dev.mufadev.storyapp.data.repo.MainRepository

class RegisterViewModel(private val pref: MainRepository) : ViewModel() {

    fun register(name: String, email: String, password: String) = pref.register(name, email, password)

}