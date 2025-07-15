// File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/ImageStorageManager.kt
package com.jetbrains.kmpapp

import kotlinx.serialization.Serializable

@Serializable
data class SavedImageData(
    val id: String,
    val originalUri: String,
    val savedPath: String,
    val name: String,
    val timestamp: Long
)

expect class ImageStorageManager {
    suspend fun saveImage(id: String, uri: String, name: String): Result<SavedImageData>
    fun loadSavedImages(): List<SavedImageData>
    suspend fun deleteImage(imageData: SavedImageData): Result<Unit>
    suspend fun deleteAllImages(): Result<Unit>
    fun isImageFileExists(savedPath: String): Boolean
    fun getDisplayUri(savedPath: String): String
}