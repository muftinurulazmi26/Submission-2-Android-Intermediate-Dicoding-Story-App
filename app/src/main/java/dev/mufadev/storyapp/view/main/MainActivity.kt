package dev.mufadev.storyapp.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dev.mufadev.storyapp.adapter.LoadingStateAdapter
import dev.mufadev.storyapp.adapter.StoryAdapter
import dev.mufadev.storyapp.data.network.ApiConfig
import dev.mufadev.storyapp.databinding.ActivityMainBinding
import dev.mufadev.storyapp.data.repo.MainRepository
import dev.mufadev.storyapp.view.addstory.AddStoryActivity
import dev.mufadev.storyapp.view.ViewModelFactory
import dev.mufadev.storyapp.view.custom.CustomDialog
import dev.mufadev.storyapp.view.login.LoginActivity
import dev.mufadev.storyapp.view.login.LoginViewModel
import dev.mufadev.storyapp.view.maps.StoryMapsActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(MainRepository.getInstance(dataStore, ApiConfig.getApiService()))
    }
    private lateinit var storyAdapter: StoryAdapter
    protected var dialog_loader: CustomDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.actionStoryLocation.setOnClickListener {
            val intent = Intent(this@MainActivity, StoryMapsActivity::class.java)
            startActivity(intent)
        }

        binding.actionAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.actionLogout.setOnClickListener {
            loginViewModel.logout()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.actionSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun setupView() {
        storyAdapter = StoryAdapter()
        dialog_loader = CustomDialog(this)
        with(binding.rvStory){
            setHasFixedSize(true)
            adapter = storyAdapter
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter{
                    storyAdapter.retry()
                }
            )
        }
        loginViewModel.getToken().observe(this){ token->
            mainViewModel.getStories("Bearer ${token}").observe(this){ stories ->
                storyAdapter.submitData(lifecycle, stories)
            }
        }

    }
}