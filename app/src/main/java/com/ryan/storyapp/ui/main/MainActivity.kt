package com.ryan.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.ryan.storyapp.R
import com.ryan.storyapp.databinding.ActivityMainBinding
import com.ryan.storyapp.repository.MainActivityRepository
import com.ryan.storyapp.repository.StoryRepository
import com.ryan.storyapp.ui.auth.AuthorizationActivity
import com.ryan.storyapp.ui.createstory.CreateStoryActivity
import com.ryan.storyapp.ui.maps.MapsActivity
import com.ryan.storyapp.viewmodel.MainActivityViewModel
import com.ryan.storyapp.viewmodel.MainActivityViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel

    private lateinit var appBar: AppBarLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vieModelInitialisation()
        setupAuthorization()
        setAppbar()
        navSettings()

        binding.fab.setOnClickListener {
            navigateToCreateStory()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun navSettings() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)
    }


    private fun navigateToAuthorization() {
        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToCreateStory() {
        val intent = Intent(this, CreateStoryActivity::class.java)
        startActivity(intent)
    }

    private fun setupAuthorization() {
        if (!mainActivityViewModel.isLogin()) {
            navigateToAuthorization()
        }
    }

    private fun vieModelInitialisation() {
        val repository = MainActivityRepository(this)
        val viewModelFactory = MainActivityViewModelFactory(repository)
        mainActivityViewModel = ViewModelProvider(this, viewModelFactory)[MainActivityViewModel::class.java]
    }

    private fun setAppbar() {
        appBar = binding.appBarLayout
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }
}
