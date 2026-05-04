package com.androidforge.streakhabit.core.common

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    object Empty : Result<Nothing>() // For cases where data is successfully fetched but there's no content
    object Offline : Result<Nothing>() // Specific state for network unavailability
}