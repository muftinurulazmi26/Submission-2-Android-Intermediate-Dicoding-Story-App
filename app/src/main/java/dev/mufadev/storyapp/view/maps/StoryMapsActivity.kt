package dev.mufadev.storyapp.view.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.mufadev.storyapp.R
import dev.mufadev.storyapp.data.network.ApiConfig
import dev.mufadev.storyapp.databinding.ActivityStoryMapsBinding
import dev.mufadev.storyapp.data.repo.MainRepository
import dev.mufadev.storyapp.utils.Helper
import dev.mufadev.storyapp.view.ViewModelFactory
import dev.mufadev.storyapp.view.custom.CustomDialog
import dev.mufadev.storyapp.view.login.LoginViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityStoryMapsBinding
    private lateinit var storyMapsViewModel: StoryMapsViewModel
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(MainRepository.getInstance(dataStore, ApiConfig.getApiService()))
    }
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var maps: GoogleMap
    private var dialog_loader: CustomDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupView()
    }

    private fun setupViewModel() {
        dialog_loader = CustomDialog(this)
        storyMapsViewModel = ViewModelProvider(
            this,
            ViewModelFactory(MainRepository.getInstance(dataStore,ApiConfig.getApiService()))
        )[StoryMapsViewModel::class.java]
    }

    private fun setupView() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.story_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                maps.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                maps.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                maps.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                maps.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        maps = googleMap
        maps.uiSettings.isZoomControlsEnabled = true
        maps.uiSettings.isIndoorLevelPickerEnabled = true
        maps.uiSettings.isCompassEnabled = true
        maps.uiSettings.isMapToolbarEnabled = true

        maps.setOnPoiClickListener { poi ->
            val pMarker = maps.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            )
            pMarker?.showInfoWindow()
        }

        loginViewModel.getToken().observe(this){ token ->
            storyMapsViewModel.getStoriesWithMap("Bearer ${token}").observe(this){ result ->
                if (result != null){
                    when(result) {
                        is dev.mufadev.storyapp.data.Result.Loading -> {
                            showLoading()
                        }
                        is dev.mufadev.storyapp.data.Result.Success -> {
                            hideLoading()
                            result.data.listStory.map {
                                if (it.lat != null && it.lon != null){
                                    val location = LatLng(
                                        it.lat!!.toDouble(),
                                        it.lon!!.toDouble()
                                    )
                                    maps.addMarker(
                                        MarkerOptions().position(location)
                                            .title(it.name)
                                            .snippet(it.description)
                                            .icon(Helper.vectorToBitmap(this, R.drawable.location_marker))
                                    )
                                }
                            }
                        }
                        is dev.mufadev.storyapp.data.Result.Error -> {
                            hideLoading()
                            Toast.makeText(this@StoryMapsActivity, result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        getMyLocation()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            maps.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    maps.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
}