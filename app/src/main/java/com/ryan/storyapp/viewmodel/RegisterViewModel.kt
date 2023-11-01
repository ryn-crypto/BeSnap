package com.ryan.storyapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.data.model.AuthRegisterResponse
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.repository.RegisterRepository

class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<ResultViewModel<AuthRegisterResponse>?>()
    val registerResult: MutableLiveData<ResultViewModel<AuthRegisterResponse>?>
        get() = _registerResult

    fun resetResult() {
        _registerResult.value = null
    }

    fun registerUser(name: String, email: String, password: String) {
        repository.registerUser(name, email, password) { result ->
            _registerResult.postValue(result)
        }
    }
}

class RegisterViewModelFactory(private val repository: RegisterRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}