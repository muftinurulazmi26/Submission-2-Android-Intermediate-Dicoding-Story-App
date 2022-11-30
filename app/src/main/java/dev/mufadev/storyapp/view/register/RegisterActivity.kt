package dev.mufadev.storyapp.view.register

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
import dev.mufadev.storyapp.data.network.ApiConfig
import dev.mufadev.storyapp.databinding.ActivityRegisterBinding
import dev.mufadev.storyapp.data.repo.MainRepository
import dev.mufadev.storyapp.data.response.RegisterResponse
import dev.mufadev.storyapp.view.ViewModelFactory
import dev.mufadev.storyapp.view.custom.CustomDialog
import dev.mufadev.storyapp.view.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    protected var dialog_loader: CustomDialog? = null
    private lateinit var registerResponse: RegisterResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(MainRepository.getInstance(dataStore, ApiConfig.getApiService()))
        )[RegisterViewModel::class.java]
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -35f,35f).apply{
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val etName = ObjectAnimator.ofFloat(binding.layoutNama, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.layoutEmail, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.layoutPassword, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(etName, etEmail, etPassword, btnRegister)
            start()
        }
    }

    private fun setupView() {
        dialog_loader = CustomDialog(this)
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.layoutNama.error = "Masukkan name"
                }
                email.isEmpty() -> {
                    binding.layoutEmail.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.layoutPassword.error = "Masukkan password"
                }
                else -> {
                    registerViewModel.register(name, email, password).observe(this){
                        if (it != null){
                            when(it){
                                is dev.mufadev.storyapp.data.Result.Loading -> {
                                    showLoading()
                                }
                                is dev.mufadev.storyapp.data.Result.Success -> {
                                    hideLoading()
                                    registerResponse = it.data
                                    if (registerResponse.error){
                                        Toast.makeText(this@RegisterActivity, registerResponse.message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                                is dev.mufadev.storyapp.data.Result.Error -> {
                                    hideLoading()
                                    Toast.makeText(this@RegisterActivity, getString(R.string.register_error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
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