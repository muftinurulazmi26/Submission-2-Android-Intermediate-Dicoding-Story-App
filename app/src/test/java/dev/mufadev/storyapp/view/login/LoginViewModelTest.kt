package dev.mufadev.storyapp.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import dev.mufadev.storyapp.data.repo.MainRepository
import dev.mufadev.storyapp.data.response.LoginResponse
import dev.mufadev.storyapp.MainCoroutineRule
import dev.mufadev.storyapp.view.getOrAwaitValue
import dev.mufadev.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class LoginViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mainRepository: MainRepository
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()

    private val dummyEmail = "muftitestt@gmail.com"
    private val dummyPassword = "qwertty"

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Login success and Result Success`(){
        val expectedLoginResponse = MutableLiveData<dev.mufadev.storyapp.data.Result<LoginResponse>>()
        expectedLoginResponse.value = dev.mufadev.storyapp.data.Result.Success(dummyLoginResponse)

        `when`(mainRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedLoginResponse)

        val loginViewModel = LoginViewModel(mainRepository)
        val actualLoginResponse = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(mainRepository).login(dummyEmail, dummyPassword)
        Assert.assertNotNull(actualLoginResponse)
        Assert.assertTrue(actualLoginResponse is dev.mufadev.storyapp.data.Result.Success)
        Assert.assertSame(dummyLoginResponse,(actualLoginResponse as dev.mufadev.storyapp.data.Result.Success).data)
    }

    @Test
    fun `Login failed and Result Error`(){
        val loginResponse = MutableLiveData<dev.mufadev.storyapp.data.Result<LoginResponse>>()
        loginResponse.value = dev.mufadev.storyapp.data.Result.Error("Error")

        `when`(mainRepository.login(dummyEmail, dummyPassword)).thenReturn(loginResponse)

        val loginViewModel = LoginViewModel(mainRepository)
        val actualLoginResponse = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(mainRepository).login(dummyEmail, dummyPassword)
        Assert.assertNotNull(actualLoginResponse)
        Assert.assertTrue(actualLoginResponse is dev.mufadev.storyapp.data.Result.Error)
    }

    @Test
    fun `Save data user successfully`() = mainCoroutineRule.runBlockingTest {
        val loginViewModel = LoginViewModel(mainRepository)
        loginViewModel.saveUser(DataDummy.generatedDummySessionUser())
        Mockito.verify(mainRepository).saveuser(DataDummy.generatedDummySessionUser())
    }

    @Test
    fun `Get Session user successfully`() = runTest{
        val loginViewModel = LoginViewModel(mainRepository)
        loginViewModel.getUser()
        Mockito.verify(mainRepository).getUser()
    }

    @Test
    fun `Save token successfully`() = mainCoroutineRule.runBlockingTest {
        val loginViewModel = LoginViewModel(mainRepository)
        loginViewModel.setToken(DataDummy.token)
        Mockito.verify(mainRepository).setToken(DataDummy.token)
    }

    @Test
    fun `Set logout successfully`() = mainCoroutineRule.runBlockingTest {
        val loginViewModel = LoginViewModel(mainRepository)
        loginViewModel.logout()
        Mockito.verify(mainRepository).logout()
    }
}