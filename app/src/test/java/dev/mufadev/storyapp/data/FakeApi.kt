package dev.mufadev.storyapp.data

import dev.mufadev.storyapp.data.network.ApiService
import dev.mufadev.storyapp.data.response.LoginResponse
import dev.mufadev.storyapp.data.response.RegisterResponse
import dev.mufadev.storyapp.data.response.StoryResponse
import dev.mufadev.storyapp.data.response.UploadStoryResponse
import dev.mufadev.storyapp.utils.DataDummy
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApi : ApiService {
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyStories = DataDummy.generateDummyStoriesResponse()
    private val dummyUploadStory = DataDummy.generateDummyUploadStoryResponse()

    override suspend fun addUser(name: String, email: String, password: String): RegisterResponse {
        return dummyRegisterResponse
    }

    override suspend fun getUser(email: String, password: String): LoginResponse {
        return dummyLoginResponse
    }

    override suspend fun addStory(
        description: RequestBody,
        file: MultipartBody.Part,
        token: String,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): UploadStoryResponse {
        return dummyUploadStory
    }

    override suspend fun getAllStory(token: String, page: Int, size: Int): StoryResponse {
        return dummyStories
    }

    override suspend fun getStoriesMapsLocation(token: String): StoryResponse {
        return dummyStories
    }
}