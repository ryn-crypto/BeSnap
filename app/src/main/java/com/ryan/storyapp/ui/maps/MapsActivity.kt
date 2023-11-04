package com.ryan.storyapp.ui.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.util.Util
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.databinding.ActivityMapsBinding
import com.ryan.storyapp.repository.MapsRepository
import com.ryan.storyapp.viewmodel.MapsViewModel
import com.ryan.storyapp.viewmodel.MapsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vieModelInitialisation()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val initialMark = LatLng(-6.8957643, 107.6338462)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialMark, 5f))

        getMyLocation()
        addManyMarker()
        setMapStyle()

        mMap.setOnMyLocationButtonClickListener {
            if (isGpsEnabled()) {
                false
            } else {
                showGpsDisabledDialog()
                true
            }
        }
    }

    private fun addManyMarker() {

        mapsViewModel.result.observe(this) { result ->
            when (result) {
                is ResultViewModel.Success -> {
                    val data = result.data
                    addMarkersToMap(data)
                }

                is ResultViewModel.Error -> {
                    val errorMessage = result.message
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val errorMessage = getString(R.string.error)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun vieModelInitialisation() {
        val repository = MapsRepository(this)
        val viewModelFactory = MapsViewModelFactory(repository)
        mapsViewModel = ViewModelProvider(this, viewModelFactory)[MapsViewModel::class.java]
        mapsViewModel.fetchData()
    }

    private fun addMarkersToMap(snapData: List<ListStoryItem>) {
        snapData.forEach { data ->
            val location = LatLng(data.lat!!, data.lon!!)
            val markerOptions = MarkerOptions()
                .position(location)
                .title(data.name)
                .snippet(data.description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .alpha(0.9f)

            mMap.addMarker(markerOptions)
        }
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGpsDisabledDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.gps_is_disabled))
            .setMessage(getString(R.string.enable_gps_warning))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find style. Error: ", exception)
        }
    }
}