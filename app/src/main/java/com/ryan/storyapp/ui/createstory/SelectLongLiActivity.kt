package com.ryan.storyapp.ui.createstory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ryan.storyapp.R
import com.ryan.storyapp.databinding.ActivitySelectLongLiBinding
import kotlinx.coroutines.Dispatchers
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
        setMapStyle()

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
            val context = this

            if (isLocationEnabled(context)) {
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
            } else {
                showEnableLocationDialog(context)
                false
            }
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
        binding.description.text = getString(R.string.select_location_warning)
        binding.progressIndicator.visibility = View.GONE
    }

    private fun showToast() {
            Toast.makeText(this, getString(R.string.location_selected), Toast.LENGTH_SHORT).show()
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showEnableLocationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.gps_is_disabled))
        builder.setMessage(getString(R.string.enable_gps_warning))
        builder.setPositiveButton(getString(R.string.enable_gps)) { _, _ ->
            context.startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        builder.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
        val dialog = builder.create()
        dialog.show()
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