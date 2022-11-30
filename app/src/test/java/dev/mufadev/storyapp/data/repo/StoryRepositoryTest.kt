package dev.mufadev.storyapp.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import dev.mufadev.storyapp.MainCoroutineRule
import dev.mufadev.storyapp.PagingDataTest
import dev.mufadev.storyapp.adapter.StoryAdapter
import dev.mufadev.storyapp.data.FakeApi
import dev.mufadev.storyapp.data.local.entity.Story
import dev.mufadev.storyapp.data.local.room.StoryDatabase
import dev.mufadev.storyapp.data.network.ApiService
import dev.mufadev.storyapp.utils.DataDummy
import dev.mufadev.storyapp.view.getOrAwaitValue
import dev.mufadev.storyapp.view.main.MainViewModel
import dev.mufadev.storyapp.view.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyDatabase: StoryDatabase
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var storyRepository: StoryRepository
    private val dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    private val dummyFile = DataDummy.generateDummyMultipartFile()
    private val dummyRequestBody = DataDummy.generateDummyRequestBody()

    @Before
    fun setUp(){
        apiService = FakeApi()
        storyRepository = StoryRepository(storyDatabase, apiService)
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Get stories should not null`() = mainCoroutineRule.runBlockingTest {
        val actualResponse = storyRepository.getStoryPaging(dummyToken)

        actualResponse.observeForTesting {
            Assert.assertNotNull(actualResponse)
        }
    }

    @Test
    fun `Upload story successfully`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = DataDummy.generateDummyUploadStoryResponse()
        val actualResponse = storyRepository.uploadStory(dummyToken, dummyFile, dummyRequestBody, null, null)

        actualResponse.observeForTesting {
            Assert.assertNotNull(expectedResponse)
            Assert.assertEquals(expectedResponse.message, (actualResponse.value as dev.mufadev.storyapp.data.Result.Success).data.message)
        }
    }
}