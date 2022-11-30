package dev.mufadev.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import dev.mufadev.storyapp.adapter.StoryAdapter
import dev.mufadev.storyapp.data.local.entity.Story
import dev.mufadev.storyapp.MainCoroutineRule
import dev.mufadev.storyapp.PagingDataTest
import dev.mufadev.storyapp.data.paging.StoryPagingSource
import dev.mufadev.storyapp.data.repo.MainRepository
import dev.mufadev.storyapp.data.repo.StoryRepository
import dev.mufadev.storyapp.view.getOrAwaitValue
import dev.mufadev.storyapp.utils.DataDummy
import dev.mufadev.storyapp.view.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
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
class MainViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private val dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWlZbHdrZ21DRndYM0tVVXMiLCJpYXQiOjE2NjYzNjA2ODN9.dNTSWDgq0awV8SjaL4b5582nikxN6-tcjpnSYDcgUEw"

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `When get list story should not null`()= mainCoroutineRule.runBlockingTest{
        val dummyStories = DataDummy.generateDummyStoriesList()
        val data : PagingData<Story> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<Story>>()
        expectedStories.value = data
        `when`(storyRepository.getStoryPaging(dummyToken)).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(storyRepository)
        val actualStories = mainViewModel.getStories(dummyToken).getOrAwaitValue()

        val storyDiffer = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
            )

        storyDiffer.submitData(actualStories)

        Mockito.verify(storyRepository).getStoryPaging(dummyToken)
        Assert.assertNotNull(actualStories)
        Assert.assertEquals(dummyStories.size, storyDiffer.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}