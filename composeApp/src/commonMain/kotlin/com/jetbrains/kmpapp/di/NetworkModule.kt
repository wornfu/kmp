// File: commonMain/kotlin/com/jetbrains/kmpapp/di/NetworkModule.kt
package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.network.ApiClient
import com.jetbrains.kmpapp.network.AuthService

object NetworkModule {

    private val apiClient by lazy { ApiClient() }

    val authService by lazy { AuthService(apiClient) }

    // Add other services here as needed
    // val documentService by lazy { DocumentService(apiClient) }
    // val notificationService by lazy { NotificationService(apiClient) }
}