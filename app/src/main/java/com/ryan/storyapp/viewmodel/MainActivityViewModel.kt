package com.ryan.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.repository.MainActivityRepository

class MainActivityViewModel(private val repository: MainActivityRepository) : ViewModel() {

    fun isLogin(): Boolean {
        return repository.isLogin()
    }
}

class MainActivityViewModelFactory(private val repository: MainActivityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}