package dev.mufadev.storyapp.data.response

import com.google.gson.annotations.SerializedName
import dev.mufadev.storyapp.data.local.entity.Story

data class StoryResponse(
    @field:SerializedName("error")
    val error: Boolean?,
    @field:SerializedName("message")
    val message: String?,
    @field:SerializedName("listStory")
    val listStory: List<Story>
)