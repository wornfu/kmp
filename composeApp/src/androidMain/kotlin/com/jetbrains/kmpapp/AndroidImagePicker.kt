// File: composeApp/src/androidMain/kotlin/com/jetbrains/kmpapp/AndroidImagePicker.kt
package com.jetbrains.kmpapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.jetbrains.kmpapp.screens.images.ImagePicker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AndroidImagePicker(private val activity: ComponentActivity) : ImagePicker {

    private var onGalleryResult: ((String?) -> Unit)? = null
    private var onCameraResult: ((String?) -> Unit)? = null
    private var tempPhotoUri: Uri? = null
    private val permissionHandler = PermissionHandler(activity)

    // Gallery picker launcher
    private val galleryLauncher = activity.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onGalleryResult?.invoke(uri?.toString())
        onGalleryResult = null
    }

    // Camera launcher
    private val cameraLauncher = activity.registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            onCameraResult?.invoke(tempPhotoUri?.toString())
        } else {
            onCameraResult?.invoke(null)
        }
        onCameraResult = null
        tempPhotoUri = null
    }

    override fun pickFromGallery(onResult: (String?) -> Unit) {
        permissionHandler.requestGalleryPermissions { granted ->
            if (granted) {
                onGalleryResult = onResult
                galleryLauncher.launch("image/*")
            } else {
                onResult(null)
            }
        }
    }

    override fun takePhoto(onResult: (String?) -> Unit) {
        permissionHandler.requestCameraPermissions { granted ->
            if (granted) {
                onCameraResult = onResult

                // สร้างไฟล์ temporary สำหรับเก็บรูปจากกล้อง
                val photoFile = createImageFile()
                tempPhotoUri = FileProvider.getUriForFile(
                    activity,
                    "${activity.packageName}.provider",
                    photoFile
                )

                cameraLauncher.launch(tempPhotoUri!!)
            } else {
                onResult(null)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(activity.getExternalFilesDir(null), "pictures")

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File(storageDir, "IMG_${timeStamp}.jpg")
    }
}