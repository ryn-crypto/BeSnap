package com.ryan.storyapp.ui.createstory

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.R
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.databinding.ActivityCreateStoryBinding
import com.ryan.storyapp.repository.CreateStoryRepository
import com.ryan.storyapp.ui.createstory.CameraActivity.Companion.CAMERAX_RESULT
import com.ryan.storyapp.ui.main.MainActivity
import com.ryan.storyapp.utils.reduceFileImage
import com.ryan.storyapp.utils.uriToFile
import com.ryan.storyapp.viewmodel.CreateStoryViewModel
import com.ryan.storyapp.viewmodel.CreateStoryViewModelFactory

class CreateStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var createStoryViewModel: CreateStoryViewModel
    private var imageUri: Uri? = null
    private val REQUEST_SELECT_LOCATION = 123
    private var currentLongitude: Double = 0.0
    private var currentLatitude: Double = 0.0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.granted_p), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.denied_p), Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initiationViewModel()
        setToolbar()

        binding.gallery.setOnClickListener {
            openGallery()
        }

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.camera.setOnClickListener {
            startCameraX()
        }

        binding.buttonAdd.setOnClickListener {
            checkLongLi()
            showLoading(true)
            uploadImage()
        }

        binding.maps.setOnClickListener { v ->
            showPopupMenu(v)
        }
    }

    private fun setToolbar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.create_story_title)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherIntentGallery.launch(intent)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val selectedImage = it.data?.data

            selectedImage?.let { uri ->
                imageUri = uri
                showImage()
            }
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()?.let { uri ->
                imageUri = uri
                showImage()
            }
        }
    }

    private fun showImage() {
        imageUri.let {
            binding.preview.setPadding(0, 0, 0, 0)
            binding.preview.setImageURI(it)
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_LOCATION && resultCode == Activity.RESULT_OK) {
            val longitude = data?.getDoubleExtra("longitude", 0.0)
            val latitude = data?.getDoubleExtra("latitude", 0.0)

            currentLongitude = longitude!!
            currentLatitude = latitude!!
        }
    }


    private fun mapSelect() {
        val intent = Intent(this, SelectLongLiActivity::class.java)
        startActivityForResult(intent, REQUEST_SELECT_LOCATION)
    }

    private fun uploadImage() {
        val description = binding.edAddDescription.text.toString()

        if (description.isNotEmpty()) {
            imageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()

                createStoryViewModel.createStory(
                    description,
                    imageFile,
                    currentLongitude,
                    currentLatitude
                )

                createStoryViewModel.createStoryResult.observe(this) { result ->
                    when (result) {
                        is ResultViewModel.Success -> {
                            showLoading(false)
                            val response = result.data
                            showToast(response.message.toString())
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }

                        is ResultViewModel.Error -> {
                            showLoading(false)
                            val errorMessage = result.message
                            showToast(errorMessage)
                            createStoryViewModel.resetResult()
                        }

                        else -> {
                            showLoading(false)
                        }
                    }
                }
            } ?: also {
                showToast(getString(R.string.empty_image_warning))
                showLoading(false)
            }
        } else {
            showToast(getString(R.string.empty_desc_warning))
            showLoading(false)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressIndicator.visibility = View.VISIBLE
                lottieAnimationView.visibility = View.VISIBLE
                buttonAdd.visibility = View.INVISIBLE
            } else {
                progressIndicator.visibility = View.INVISIBLE
                lottieAnimationView.visibility = View.INVISIBLE
                buttonAdd.visibility = View.VISIBLE
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initiationViewModel() {
        val repository = CreateStoryRepository(this)
        val viewModelFactory = CreateStoryViewModelFactory(repository)
        createStoryViewModel =
            ViewModelProvider(this, viewModelFactory)[CreateStoryViewModel::class.java]
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.map_manu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.withMap -> {
                    binding.maps.setImageResource(R.drawable.location)
                    mapSelect()
                    return@setOnMenuItemClickListener true
                }

                R.id.withoutMap -> {
                    currentLatitude = 0.0
                    currentLongitude = 0.0
                    binding.maps.setImageResource(R.drawable.location_off)
                    showToast("You did not use a location for this snap")
                    return@setOnMenuItemClickListener true
                }
                // Add more items as needed
                else -> return@setOnMenuItemClickListener false
            }
        }

        popupMenu.show()
    }

    private fun checkLongLi() {
        if (currentLatitude == 0.0) {
            binding.maps.setImageResource(R.drawable.location_off)
        }
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}