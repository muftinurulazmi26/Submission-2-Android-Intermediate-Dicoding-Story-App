package dev.mufadev.storyapp.view.login

import androidx.lifecycle.*
import dev.mufadev.storyapp.data.local.entity.UserModel
import dev.mufadev.storyapp.data.repo.MainRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: MainRepository): ViewModel() {

    fun login(email: String, password: String) = pref.login(email, password)

    fun saveUser(user: UserModel){
        viewModelScope.launch {
            pref.saveuser(user)
        }
    }

    fun getUser() = pref.getUser()

    fun setToken(token: String){
        viewModelScope.launch {
            pref.setToken(token)
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun isLogin() : LiveData<Boolean>{
        return pref.isLogin().asLiveData()
    }

    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }

    fun logout(){
        viewModelScope.launch {
            pref.logout()
        }
    }
}