package dev.mufadev.storyapp.utils

import dev.mufadev.storyapp.data.local.entity.Story
import dev.mufadev.storyapp.data.local.entity.UserModel
import dev.mufadev.storyapp.data.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {
    fun generateDummyStoriesResponse(): StoryResponse {
        val stories = arrayListOf<Story>()

        for (i in 0 until 10){
            val story = Story(
                "story-4stHTSLEi9jy1rV4",
                "mufti",
                "qwertyuiop",
                "https://story-api.dicoding.dev/images/stories/photos-1666356053282_jK4lcjg6.jpg",
                "2022-10-21T12:40:53.284Z",
                (-6.9830533).toFloat(),
                (109.1365083).toFloat()
            )
            stories.add(story)
        }
        return StoryResponse(false,"Stories fetched successfully",stories)
    }

    fun generateDummyUploadStoryResponse(): UploadStoryResponse {
        return UploadStoryResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyLoginResponse(): LoginResponse{
        val loginResult = LoginResult(
            userId = "user-iYlwkgmCFwX3KUUs",
            name = "mufti",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWlZbHdrZ21DRndYM0tVVXMiLCJpYXQiOjE2NjYzNjA2ODN9.dNTSWDgq0awV8SjaL4b5582nikxN6-tcjpnSYDcgUEw"
        )

        return LoginResponse(
            loginResult = loginResult,
            error = false,
            message = "success"
        )
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyMultipartFile(): MultipartBody.Part{
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateDummyRequestBody(): RequestBody {
        val dummyText = "text"
        return dummyText.toRequestBody()
    }

    fun generateDummyStoriesList(): List<Story>{
        val stories = arrayListOf<Story>()

        for (i in 0 until 5){
            val story = Story(
                "story-4stHTSLEi9jy1rV4",
                "mufti",
                "qwertyuiop",
                "https://story-api.dicoding.dev/images/stories/photos-1666356053282_jK4lcjg6.jpg",
                "2022-10-21T12:40:53.284Z",
                (-6.9830533).toFloat(),
                (109.1365083).toFloat()
            )

            stories.add(story)
        }
        return stories
    }

    fun generatedDummySessionUser(): UserModel {
        return UserModel(
            userId = "user-iYlwkgmCFwX3KUUs",
            name = "mufti",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWlZbHdrZ21DRndYM0tVVXMiLCJpYXQiOjE2NjYzNjA2ODN9.dNTSWDgq0awV8SjaL4b5582nikxN6-tcjpnSYDcgUEw",
            isLogin = true
        )
    }

    val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWlZbHdrZ21DRndYM0tVVXMiLCJpYXQiOjE2NjYzNjA2ODN9.dNTSWDgq0awV8SjaL4b5582nikxN6-tcjpnSYDcgUEw"
}