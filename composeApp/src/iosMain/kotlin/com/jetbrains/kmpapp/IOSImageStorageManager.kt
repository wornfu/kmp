// File: composeApp/src/iosMain/kotlin/com/jetbrains/kmpapp/IOSImageStorageManager.kt
package com.jetbrains.kmpapp

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UIKit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalForeignApi::class)
class IOSImageStorageManager {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val imageListKey = "saved_images_list_ios"

    private val documentsDirectory: String by lazy {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        )
        paths.firstOrNull() as? String ?: ""
    }

    /**
     * บันทึกรูปภาพลงใน Documents Directory
     */
    suspend fun saveImage(
        id: String,
        uri: String,
        name: String
    ): Result<SavedImageData> = withContext(Dispatchers.Default) {
        try {
            // ตรวจสอบว่า URI เป็น file path หรือไม่
            val sourcePath = if (uri.startsWith("file://")) {
                uri.removePrefix("file://")
            } else {
                uri
            }

            // อ่านข้อมูลจากไฟล์ต้นฉบับ
            val sourceData = NSData.dataWithContentsOfFile(sourcePath)
                ?: return@withContext Result.failure(Exception("Cannot read source image file"))

            // สร้าง path ใหม่ใน Documents Directory
            val timestamp = NSDate().timeIntervalSince1970.toLong()
            val fileName = "${id}_${timestamp}.jpg"
            val destinationPath = "$documentsDirectory/$fileName"

            // บันทึกไฟล์
            val success = sourceData.writeToFile(destinationPath, atomically = true)

            if (!success) {
                return@withContext Result.failure(Exception("Failed to save image file"))
            }

            // สร้างข้อมูลรูปภาพ
            val imageData = SavedImageData(
                id = id,
                originalUri = uri,
                savedPath = destinationPath,
                name = name,
                timestamp = timestamp
            )

            // บันทึกลงใน UserDefaults
            saveImageDataToUserDefaults(imageData)

            Result.success(imageData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * โหลดรูปภาพทั้งหมดที่บันทึกไว้
     */
    fun loadSavedImages(): List<SavedImageData> {
        return try {
            val jsonString = userDefaults.stringForKey(imageListKey) ?: "[]"
            Json.decodeFromString<List<SavedImageData>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * ลบรูปภาพ
     */
    suspend fun deleteImage(imageData: SavedImageData): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            // ลบไฟล์
            val fileManager = NSFileManager.defaultManager
            val fileExists = fileManager.fileExistsAtPath(imageData.savedPath)

            if (fileExists) {
                val success = fileManager.removeItemAtPath(imageData.savedPath, error = null)
                if (!success) {
                    return@withContext Result.failure(Exception("Failed to delete image file"))
                }
            }

            // ลบจาก UserDefaults
            removeImageDataFromUserDefaults(imageData.id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * ลบรูปภาพทั้งหมด
     */
    suspend fun deleteAllImages(): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            val images = loadSavedImages()
            val fileManager = NSFileManager.defaultManager

            // ลบไฟล์ทั้งหมด
            images.forEach { imageData ->
                if (fileManager.fileExistsAtPath(imageData.savedPath)) {
                    fileManager.removeItemAtPath(imageData.savedPath, error = null)
                }
            }

            // เคลียร์ UserDefaults
            userDefaults.removeObjectForKey(imageListKey)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveImageDataToUserDefaults(imageData: SavedImageData) {
        val currentList = loadSavedImages().toMutableList()

        // ลบรายการเก่าที่มี id เดียวกัน (ถ้ามี)
        currentList.removeAll { it.id == imageData.id }

        // เพิ่มรายการใหม่
        currentList.add(imageData)

        // บันทึกกลับ
        val jsonString = Json.encodeToString(currentList)
        userDefaults.setObject(jsonString, forKey = imageListKey)
    }

    private fun removeImageDataFromUserDefaults(imageId: String) {
        val currentList = loadSavedImages().toMutableList()
        currentList.removeAll { it.id == imageId }

        val jsonString = Json.encodeToString(currentList)
        userDefaults.setObject(jsonString, forKey = imageListKey)
    }

    /**
     * ตรวจสอบว่าไฟล์ยังอยู่หรือไม่
     */
    fun isImageFileExists(savedPath: String): Boolean {
        return NSFileManager.defaultManager.fileExistsAtPath(savedPath)
    }

    /**
     * รับ URI สำหรับแสดงผล
     */
    fun getDisplayUri(savedPath: String): String {
        return "file://$savedPath"
    }
}