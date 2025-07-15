package com.jetbrains.kmpapp.network

// File: commonMain/kotlin/com/jetbrains/kmpapp/network/ApiClient.kt
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.call.*

class ApiClient {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }

        defaultRequest {
            // Add your base URL here
            url("https://your-api-base-url.com/")

            // Add common headers
            header("Content-Type", "application/json")
            header("Accept", "application/json")
        }
    }

    suspend inline fun <reified T> safeApiCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): NetworkResult<T> {
        return try {
            val response = apiCall()
            when (response.status) {
                HttpStatusCode.OK -> {
                    val data = response.body<T>()
                    NetworkResult.Success(data)
                }
                else -> {
                    NetworkResult.Error(
                        message = "HTTP ${response.status.value}: ${response.status.description}",
                        code = response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ"
            )
        }
    }
}