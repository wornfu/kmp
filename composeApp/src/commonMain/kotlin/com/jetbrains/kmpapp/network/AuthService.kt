package com.jetbrains.kmpapp.network

// File: commonMain/kotlin/com/jetbrains/kmpapp/network/AuthService.kt

import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserInfoResponse
)

@Serializable
data class UserInfoResponse(
    val username: String,
    val fullName: String,
    val position: String,
    val unit: String
)

class AuthService(private val apiClient: ApiClient) {

    suspend fun login(username: String, password: String): NetworkResult<LoginResponse> {
        return apiClient.safeApiCall<LoginResponse> {
            apiClient.httpClient.post("auth/login") {
                setBody(LoginRequest(username, password))
            }
        }
    }

    suspend fun logout(): NetworkResult<Unit> {
        return apiClient.safeApiCall<Unit> {
            apiClient.httpClient.post("auth/logout")
        }
    }
}