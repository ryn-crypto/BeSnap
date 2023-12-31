package com.ryan.storyapp.ui.createstory

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.util.Util.isOnMainThread
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ryan.storyapp.R
import com.ryan.storyapp.databinding.ActivitySelectLongLiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectLongLiActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySelectLongLiBinding
    private var currentLongitude: Double = 0.0
    private var currentLatitude: Double = 0.0

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectLongLiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapselect) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        val initialMark = LatLng(-6.175394, 106.827186)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialMark, 10f))

        getMyLocation()


        var previousMarker: Marker? = null
        mMap.setOnPoiClickListener { pointOfInterest ->
            previousMarker?.remove()

            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )

            poiMarker?.showInfoWindow()
            previousMarker = poiMarker

            val markerLatLng = poiMarker?.position
            val longitude = markerLatLng?.longitude
            val latitude = markerLatLng?.latitude
            showButton()

            currentLatitude = latitude!!
            currentLongitude = longitude!!
        }

        mMap.setOnMyLocationButtonClickListener {
            if (isOnMainThread()) {
                val location = mMap.myLocation
                handleLocationUpdate(location)
            } else {
                lifecycleScope.launch {
                    val location = withContext(Dispatchers.IO) {
                        mMap.myLocation
                    }
                    withContext(Dispatchers.Main) {
                        handleLocationUpdate(location)
                    }
                }
            }
            false
        }

        binding.choose.setOnClickListener {
            showToast()
            sendDataBack()
        }

        lifecycleScope.launch {
            delay(4000)
            showText()
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

    private fun showButton() {
        binding.description.visibility = View.GONE
        binding.choose.visibility = View.VISIBLE

    }

    private fun sendDataBack() {
        val resultIntent = Intent()
        resultIntent.putExtra("longitude", currentLongitude)
        resultIntent.putExtra("latitude", currentLatitude)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun handleLocationUpdate(location: Location?) {
        if (location != null) {
            val longitude = location.longitude
            val latitude = location.latitude

            showButton()
            currentLatitude = latitude
            currentLongitude = longitude
        }
    }

    private fun showText() {
        binding.description.text = "Please press the 'Find Me' button or tap on the nearest building on the map to mark your location"
        binding.progressIndicator.visibility = View.GONE
    }

    private fun showToast() {
            Toast.makeText(this, "The snap location has been selected", Toast.LENGTH_SHORT).show()
    }

}