package dev.mufadev.storyapp.view.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import dev.mufadev.storyapp.data.repo.MainRepository
import dev.mufadev.storyapp.data.response.StoryResponse
import dev.mufadev.storyapp.view.getOrAwaitValue
import dev.mufadev.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class StoryMapsViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mainRepository: MainRepository
    private val dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWlZbHdrZ21DRndYM0tVVXMiLCJpYXQiOjE2NjYzNjA2ODN9.dNTSWDgq0awV8SjaL4b5582nikxN6-tcjpnSYDcgUEw"
    private val dummyStories = DataDummy.generateDummyStoriesResponse()

    @Test
    fun `When get Maps story should not null and return success`(){
        val expectedStories = MutableLiveData<dev.mufadev.storyapp.data.Result<StoryResponse>>()
        expectedStories.value = dev.mufadev.storyapp.data.Result.Success(dummyStories)
        `when`(mainRepository.getStoriesWithMap(dummyToken)).thenReturn(expectedStories)

        val storyMapsViewModel = StoryMapsViewModel(mainRepository)
        val actualStories = storyMapsViewModel.getStoriesWithMap(dummyToken).getOrAwaitValue()
        Mockito.verify(mainRepository).getStoriesWithMap(dummyToken)
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is dev.mufadev.storyapp.data.Result.Success<*>)
        Assert.assertSame(dummyStories,(actualStories as dev.mufadev.storyapp.data.Result.Success).data)
        Assert.assertEquals(dummyStories.listStory.size, actualStories.data.listStory.size)
    }

    @Test
    fun `When network error should return error`(){
        val expectedStories = MutableLiveData<dev.mufadev.storyapp.data.Result<StoryResponse>>()
        expectedStories.value = dev.mufadev.storyapp.data.Result.Error("Error")
        `when`(mainRepository.getStoriesWithMap(dummyToken)).thenReturn(expectedStories)

        val storyMapsViewModel = StoryMapsViewModel(mainRepository)
        val actualStories = storyMapsViewModel.getStoriesWithMap(dummyToken).getOrAwaitValue()
        Mockito.verify(mainRepository).getStoriesWithMap(dummyToken)
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is dev.mufadev.storyapp.data.Result.Error)
    }

}