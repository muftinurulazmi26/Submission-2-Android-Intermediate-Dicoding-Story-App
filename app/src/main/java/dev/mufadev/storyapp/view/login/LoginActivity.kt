package dev.mufadev.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import dev.mufadev.storyapp.R
import dev.mufadev.storyapp.databinding.ActivityLoginBinding
import dev.mufadev.storyapp.data.local.entity.UserModel
import dev.mufadev.storyapp.data.network.ApiConfig
import dev.mufadev.storyapp.data.repo.MainRepository
import dev.mufadev.storyapp.data.response.LoginResponse
import dev.mufadev.storyapp.view.ViewModelFactory
import dev.mufadev.storyapp.view.custom.CustomDialog
import dev.mufadev.storyapp.view.main.MainActivity
import dev.mufadev.storyapp.view.register.RegisterActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    protected var dialog_loader: CustomDialog? = null
    private lateinit var loginResponse: LoginResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -35f,35f).apply{
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val etEmail = ObjectAnimator.ofFloat(binding.layoutEmail, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.layoutPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(etEmail, etPassword, btnLogin)
            start()
        }
    }

    private fun setupView() {
        dialog_loader = CustomDialog(this)

        loginViewModel.isLogin().observe(this) {
            if (it){
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.layoutEmail.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.layoutPassword.error = "Masukkan password"
                }
                else -> {
                    loginViewModel.login(email, password).observe(this){
                        if (it != null){
                            when(it){
                                is dev.mufadev.storyapp.data.Result.Loading -> {
                                    showLoading()
                                }
                                is dev.mufadev.storyapp.data.Result.Success -> {
                                    hideLoading()
                                    loginResponse = it.data
                                    if (loginResponse.error){
                                        Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        loginViewModel.saveUser(
                                            UserModel(
                                                loginResponse.loginResult!!.userId,
                                                loginResponse.loginResult!!.name,
                                                loginResponse.loginResult!!.token))
                                        loginViewModel.login()
                                        loginViewModel.setToken(loginResponse.loginResult!!.token)
                                    }
                                }
                                is dev.mufadev.storyapp.data.Result.Error -> {
                                    hideLoading()
                                    Toast.makeText(this@LoginActivity, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(MainRepository.getInstance(dataStore, ApiConfig.getApiService()))
        )[LoginViewModel::class.java]
    }

    private fun showLoading(){
        if (!dialog_loader?.isShowing!!){
            dialog_loader?.show()
        }
    }

    private fun hideLoading(){
        if (dialog_loader?.isShowing!!){
            dialog_loader?.dismiss()
        }
    }
}