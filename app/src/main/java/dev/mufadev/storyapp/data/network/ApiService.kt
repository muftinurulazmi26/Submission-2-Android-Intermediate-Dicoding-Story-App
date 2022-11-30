package dev.mufadev.storyapp.data.network

import dev.mufadev.storyapp.data.response.LoginResponse
import dev.mufadev.storyapp.data.response.RegisterResponse
import dev.mufadev.storyapp.data.response.StoryResponse
import dev.mufadev.storyapp.data.response.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    suspend fun addUser(@Field("name") name: String,
                @Field("email")email: String,
                @Field("password")password: String): RegisterResponse

    @POST("login")
    @FormUrlEncoded
    suspend fun getUser(@Field("email")email: String,
                @Field("password")password: String): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(@Part("description")description: RequestBody,
                 @Part file: MultipartBody.Part,
                 @Header("Authorization")token: String,
                 @Part("lat") latitude: RequestBody?,
                 @Part("lon") longitude: RequestBody?): UploadStoryResponse

    @GET("stories")
    suspend fun getAllStory(
        @Header("Authorization")token: String,
        @Query("page")page: Int,
        @Query("size")size: Int): StoryResponse

    @GET("stories?location=1")
    suspend fun getStoriesMapsLocation(
        @Header("Authorization") token: String
    ): StoryResponse
}