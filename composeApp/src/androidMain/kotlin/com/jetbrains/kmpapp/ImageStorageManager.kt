// File: composeApp/src/androidMain/kotlin/com/jetbrains/kmpapp/ImageStorageManager.kt
package com.jetbrains.kmpapp

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

actual class ImageStorageManager(private val context: Context) {
    private val imagesDir = File(context.filesDir, "saved_images")
    private val prefsName = "image_storage_prefs"
    private val imageListKey = "saved_images_list"

    init {
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
    }

    private val sharedPrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    actual suspend fun saveImage(
        id: String,
        uri: String,
        name: String
    ): Result<SavedImageData> = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = when {
                uri.startsWith("content://") -> {
                    context.contentResolver.openInputStream(Uri.parse(uri))
                }
                uri.startsWith("file://") -> {
                    File(Uri.parse(uri).path!!).inputStream()
                }
                else -> null
            }

            if (inputStream == null) {
                return@withContext Result.failure(Exception("Cannot open image file"))
            }

            val fileName = "${id}_${System.currentTimeMillis()}.jpg"
            val savedFile = File(imagesDir, fileName)

            FileOutputStream(savedFile).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()

            val imageData = SavedImageData(
                id = id,
                originalUri = uri,
                savedPath = savedFile.absolutePath,
                name = name,
                timestamp = System.currentTimeMillis()
            )

            saveImageDataToPrefs(imageData)
            Result.success(imageData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual fun loadSavedImages(): List<SavedImageData> {
        return try {
            val jsonString = sharedPrefs.getString(imageListKey, "[]") ?: "[]"
            Json.decodeFromString<List<SavedImageData>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    actual suspend fun deleteImage(imageData: SavedImageData): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = File(imageData.savedPath)
            if (file.exists()) {
                file.delete()
            }
            removeImageDataFromPrefs(imageData.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun deleteAllImages(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            imagesDir.listFiles()?.forEach { file -> file.delete() }
            sharedPrefs.edit().remove(imageListKey).apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual fun isImageFileExists(savedPath: String): Boolean {
        return File(savedPath).exists()
    }

    actual fun getDisplayUri(savedPath: String): String {
        return "file://$savedPath"
    }

    private fun saveImageDataToPrefs(imageData: SavedImageData) {
        val currentList = loadSavedImages().toMutableList()
        currentList.removeAll { it.id == imageData.id }
        currentList.add(imageData)
        val jsonString = Json.encodeToString(currentList)
        sharedPrefs.edit().putString(imageListKey, jsonString).apply()
    }

    private fun removeImageDataFromPrefs(imageId: String) {
        val currentList = loadSavedImages().toMutableList()
        currentList.removeAll { it.id == imageId }
        val jsonString = Json.encodeToString(currentList)
        sharedPrefs.edit().putString(imageListKey, jsonString).apply()
    }
}
