package com.ryan.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryan.storyapp.data.model.CreateStoryResponse
import com.ryan.storyapp.data.model.ResultViewModel
import com.ryan.storyapp.repository.CreateStoryRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateStoryViewModel(private val repository: CreateStoryRepository) : ViewModel() {

    private val _createStoryResult = MutableLiveData<ResultViewModel<CreateStoryResponse>?>()
    val createStoryResult: LiveData<ResultViewModel<CreateStoryResponse>?>
        get() = _createStoryResult

    fun createStory(
        description: String, imageFile: File,
        longitude: Double, latitude: Double
    ) {

        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        val longChecked = checkNull(longitude)
        val latChecked = checkNull(latitude)

        val longitudeRequestBody =
            longChecked.toString().toRequestBody("text/plain".toMediaType())
        val latitudeRequestBody =
            latChecked.toString().toRequestBody("text/plain".toMediaType())

        if (longChecked == null) {
            repository.createStory(descriptionRequestBody, multipartBody) { result ->
                _createStoryResult.postValue(result)
            }
        } else {
            repository.createStoryWithLocation(
                descriptionRequestBody,
                multipartBody,
                longitudeRequestBody,
                latitudeRequestBody
            ) { result ->
                _createStoryResult.postValue(result)
            }
        }


    }

    fun resetResult() {
        _createStoryResult.value = null
    }
}

class CreateStoryViewModelFactory(private val repository: CreateStoryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateStoryViewModel::class.java)) {
            return CreateStoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

private fun checkNull(param: Double): Double? {
    if (param == 0.0) {
        return null
    }
    return param
}