package dev.mufadev.storyapp.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import dev.mufadev.storyapp.data.Result
import dev.mufadev.storyapp.data.local.entity.UserModel
import dev.mufadev.storyapp.data.network.ApiService
import dev.mufadev.storyapp.data.response.LoginResponse
import dev.mufadev.storyapp.data.response.RegisterResponse
import dev.mufadev.storyapp.data.response.StoryResponse
import dev.mufadev.storyapp.data.response.UploadStoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainRepository (
    private val dataStore: DataStore<Preferences>,
    private val apiService: ApiService
){

    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.addUser(name, email, password)
            emit(Result.Success(result))
        } catch (exception: Exception){
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun login(email: String, password: String) : LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.getUser(email,password)
            emit(Result.Success(result))
        } catch (exception: Exception){
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun getStoriesWithMap(token: String) : LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.getStoriesMapsLocation(token)
            emit(Result.Success(result))
        } catch (exception: Exception){
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[USER_ID_KEY] ?:"",
                preferences[NAME_KEY] ?:"",
                preferences[TOKEN_KEY] ?:"",
                preferences[LOGIN_KEY] ?:false
            )
        }
    }

    suspend fun setToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    fun isLogin() : Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_KEY] ?: false
        }
    }

    suspend fun saveuser(user: UserModel){
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.userId
            preferences[NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
            preferences[LOGIN_KEY] = user.isLogin
        }
    }

    suspend fun login(){
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MainRepository? = null

        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val LOGIN_KEY = booleanPreferencesKey("state")

        fun getInstance(
            dataStore: DataStore<Preferences>,
            apiService: ApiService
        ): MainRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MainRepository(dataStore, apiService)
                INSTANCE = instance
                instance
            }
        }
    }
}