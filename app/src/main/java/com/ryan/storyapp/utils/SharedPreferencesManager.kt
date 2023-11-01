@file:Suppress("UNCHECKED_CAST")

package com.ryan.storyapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.R

class SharedPreferencesManager(context: Context) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    fun saveApiKey(apiKey: String) {
        val editor = sharedPreferences.edit()
        editor.putString("API_KEY", apiKey)
        editor.apply()
    }

    fun getApiKey(): String? {
        return sharedPreferences.getString("API_KEY", null)
    }
}

class SharedPreferencesManagerFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedPreferencesManager::class.java)) {
            return SharedPreferencesManager(context) as T
        }
        throw IllegalArgumentException(R.string.unknownVMC.toString())
    }
}

