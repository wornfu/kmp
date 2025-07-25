
// File: commonMain/kotlin/com/jetbrains/kmpapp/network/NetworkResult.kt
package com.jetbrains.kmpapp.network

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}