package dev.mufadev.storyapp.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import dev.mufadev.storyapp.data.Result
import dev.mufadev.storyapp.data.local.entity.Story
import dev.mufadev.storyapp.data.network.ApiService
import dev.mufadev.storyapp.data.paging.StoryRemoteMediator
import dev.mufadev.storyapp.data.local.room.StoryDatabase
import dev.mufadev.storyapp.data.response.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
    ) {
    fun getStoryPaging(token: String): LiveData<PagingData<Story>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
//                StoryPagingSource(apiService, token)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun uploadStory(token: String, imageMultipart: MultipartBody.Part, desc: RequestBody, lat: RequestBody?, lon: RequestBody?): LiveData<Result<UploadStoryResponse>> = liveData{
        emit(Result.Loading)
        try {
            val client = apiService.addStory(desc, imageMultipart, token, lat, lon)
            emit(Result.Success(client))
        }catch (e : Exception){
            emit(Result.Error(e.message.toString()))
        }
    }
}