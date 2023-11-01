package com.ryan.storyapp.data.model

sealed class ResultViewModel<out T : Any> {

    data class Success<out T : Any>(val data: T) : ResultViewModel<T>()
    data class DetailSuccess(val data: Story) : ResultViewModel<Story>()
    data class Error(val message: String) : ResultViewModel<Nothing>()
    data object Loading : ResultViewModel<Nothing>()
}