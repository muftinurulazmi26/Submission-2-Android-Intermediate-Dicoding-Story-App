package dev.mufadev.storyapp.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.mufadev.storyapp.MainCoroutineRule
import dev.mufadev.storyapp.data.FakeApi
import dev.mufadev.storyapp.data.network.ApiService
import dev.mufadev.storyapp.utils.DataDummy
import dev.mufadev.storyapp.view.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainRepositoryTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var dataStore : DataStore<Preferences>
    private lateinit var mainRepository: MainRepository
    private val dummyName = "mufti"
    private val dummyEmail = "muftitestt@gmail.com"
    private val dummyPassword = "qwertty"
    private val dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"

    @Before
    fun setUp(){
        apiService = FakeApi()
        mainRepository = MainRepository(dataStore, apiService)
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Register response Should Not Null`() = mainCoroutineRule.runBlockingTest{
        val expectedResponse = DataDummy.generateDummyRegisterResponse()
        val actualResponse = mainRepository.register(dummyName, dummyEmail, dummyPassword)

        actualResponse.observeForTesting {
            Assert.assertNotNull(actualResponse)
            Assert.assertEquals(expectedResponse.message, (actualResponse.value as dev.mufadev.storyapp.data.Result.Success).data.message)
        }

    }

    @Test
    fun `Login response Should Not Null`() = mainCoroutineRule.runBlockingTest{
        val expectedResponse = DataDummy.generateDummyLoginResponse()
        val actualResponse = mainRepository.login(dummyEmail, dummyPassword)

        actualResponse.observeForTesting {
            Assert.assertNotNull(actualResponse)
            Assert.assertEquals(expectedResponse.message, (actualResponse.value as dev.mufadev.storyapp.data.Result.Success).data.message)
        }
    }

    @Test
    fun `Stories location Should Not Null`() = mainCoroutineRule.runBlockingTest {
        val expectedResponse = DataDummy.generateDummyStoriesResponse()
        val actualResponse = mainRepository.getStoriesWithMap(dummyToken)

        actualResponse.observeForTesting {
            Assert.assertNotNull(actualResponse)
            Assert.assertEquals(expectedResponse.listStory.size, (actualResponse.value as dev.mufadev.storyapp.data.Result.Success).data.listStory.size)
        }
    }
}