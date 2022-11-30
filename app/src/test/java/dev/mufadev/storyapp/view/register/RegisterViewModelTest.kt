package dev.mufadev.storyapp.view.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import dev.mufadev.storyapp.data.repo.MainRepository
import dev.mufadev.storyapp.data.response.RegisterResponse
import dev.mufadev.storyapp.view.getOrAwaitValue
import dev.mufadev.storyapp.utils.DataDummy
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mainRepository: MainRepository
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyName = "mufti"
    private val dummyEmail = "muftitestt@gmail.com"
    private val dummyPassword = "qwertty"

    @Test
    fun `Register and Result Success`(){
        val expectedRegisterResponse = MutableLiveData<dev.mufadev.storyapp.data.Result<RegisterResponse>>()
        expectedRegisterResponse.value = dev.mufadev.storyapp.data.Result.Success(dummyRegisterResponse)

        `when`(mainRepository.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedRegisterResponse)

        val registerViewModel = RegisterViewModel(mainRepository)
        val actualRegisterResponse = registerViewModel.register(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(mainRepository).register(dummyName, dummyEmail, dummyPassword)
        Assert.assertNotNull(actualRegisterResponse)
        Assert.assertTrue(actualRegisterResponse is dev.mufadev.storyapp.data.Result.Success)
        Assert.assertSame(dummyRegisterResponse,(actualRegisterResponse as dev.mufadev.storyapp.data.Result.Success).data)
    }

    @Test
    fun `Register failed and Result Error`(){
        val registerResponse = MutableLiveData<dev.mufadev.storyapp.data.Result<RegisterResponse>>()
        registerResponse.value = dev.mufadev.storyapp.data.Result.Error("Error")

        `when`(mainRepository.register(dummyName, dummyEmail, dummyPassword)).thenReturn(registerResponse)

        val registerViewModel = RegisterViewModel(mainRepository)
        val actualRegisterResponse = registerViewModel.register(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(mainRepository).register(dummyName, dummyEmail, dummyPassword)
        Assert.assertNotNull(actualRegisterResponse)
        Assert.assertTrue(actualRegisterResponse is dev.mufadev.storyapp.data.Result.Error)
    }
}