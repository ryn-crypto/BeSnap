package com.ryan.storyapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.data.model.AuthLoginResponse
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.repository.LoginRepository

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<ResultViewModel<AuthLoginResponse>?>()
    val loginResult: MutableLiveData<ResultViewModel<AuthLoginResponse>?>
        get() = _loginResult

    fun resetResult() {
        _loginResult.value = null
    }

    fun loginUser(email: String, password: String) {
        repository.loginUser(email, password) { result ->
            _loginResult.postValue(result)
        }
    }
    fun saveApiKey(apiKey: String) {
        repository.saveApiKey(apiKey)
    }
}

class LoginViewModelFactory(private val repository: LoginRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}