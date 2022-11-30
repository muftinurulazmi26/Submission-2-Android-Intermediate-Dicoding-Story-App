package dev.mufadev.storyapp.view.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dev.mufadev.storyapp.R
import dev.mufadev.storyapp.data.local.entity.UserModel
import dev.mufadev.storyapp.databinding.ActivityAddStoryBinding
import dev.mufadev.storyapp.data.repo.MainRepository
import dev.mufadev.storyapp.data.network.ApiConfig
import dev.mufadev.storyapp.utils.Helper.Companion.createCustomTempFile
import dev.mufadev.storyapp.utils.Helper.Companion.reduceFileImage
import dev.mufadev.storyapp.utils.Helper.Companion.uriToFile
import dev.mufadev.storyapp.view.ViewModelFactory
import dev.mufadev.storyapp.view.custom.CustomDialog
import dev.mufadev.storyapp.view.login.LoginViewModel
import dev.mufadev.storyapp.view.main.MainViewModel
import dev.mufadev.storyapp.view.main.MainViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var loginViewModel: LoginViewModel
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }
    protected var dialog_loader: CustomDialog? = null
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var currentLatLng: LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        setupViewModel()
        setupView()
        setupAction()
        getMyLocation()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    currentLatLng = LatLng(location.latitude, location.longitude)
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun setupView() {
        dialog_loader = CustomDialog(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupAction() {
        binding.btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)

            createCustomTempFile(application).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@AddStoryActivity,
                    "dev.mufadev.storyapp",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }

        binding.btnGallery.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }

        binding.switchLastLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked){
                binding.switchLastLocation.isChecked = true
                currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            } else {
                binding.switchLastLocation.isChecked = false
                currentLatLng = LatLng(0.0, 0.0)
            }
        }

        binding.buttonAdd.setOnClickListener {
            if (getFile != null) {
                if (!binding.switchLastLocation.isChecked){
                    currentLatLng = LatLng(0.0, 0.0)
                }
                val file = reduceFileImage(getFile as File)

                val description = "${binding.edAddDescription.text}".toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                loginViewModel.getToken().observe(this){ token->
                    mainViewModel.uploadStory(
                        "Bearer ${token}",
                        imageMultipart,
                        description,
                        currentLatLng.latitude.toString().toRequestBody("text/plain".toMediaType()),
                        currentLatLng.longitude.toString().toRequestBody("text/plain".toMediaType())
                    ).observe(this){
                        if (it != null){
                            when(it){
                                is dev.mufadev.storyapp.data.Result.Loading -> {
                                    showLoading()
                                }
                                is dev.mufadev.storyapp.data.Result.Success -> {
                                    hideLoading()
                                    Toast.makeText(this@AddStoryActivity, getString(R.string.berhasil_upload), Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                is dev.mufadev.storyapp.data.Result.Error -> {
                                    hideLoading()
                                    Toast.makeText(this@AddStoryActivity, getString(R.string.gagal_upload_story), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

            } else {
                Toast.makeText(this@AddStoryActivity, "Input Your Image First!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(MainRepository.getInstance(dataStore, ApiConfig.getApiService()))
        )[LoginViewModel::class.java]
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionGranted()){
                Toast.makeText(this, "Didn't get permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@AddStoryActivity)

            getFile = myFile

            binding.previewImage.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImage.setImageBitmap(result)
        }
    }

    fun getAddress(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lon, 1)
        return list[0].getAddressLine(0)
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

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 6
    }
}