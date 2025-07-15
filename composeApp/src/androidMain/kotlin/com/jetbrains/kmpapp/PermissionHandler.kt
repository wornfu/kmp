// File: composeApp/src/androidMain/kotlin/com/jetbrains/kmpapp/PermissionHandler.kt
package com.jetbrains.kmpapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionHandler(private val activity: ComponentActivity) {

    private var onPermissionResult: ((Boolean) -> Unit)? = null

    // Permission launcher
    private val permissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        onPermissionResult?.invoke(allGranted)
        onPermissionResult = null
    }

    fun requestCameraPermissions(onResult: (Boolean) -> Unit) {
        onPermissionResult = onResult

        val permissions = mutableListOf<String>().apply {
            add(Manifest.permission.CAMERA)

            // สำหรับ Android 13+ ใช้ READ_MEDIA_IMAGES
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // ตรวจสอบว่า permission ได้รับอนุญาตแล้วหรือไม่
        val allPermissionsGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            onResult(true)
        } else {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    fun requestGalleryPermissions(onResult: (Boolean) -> Unit) {
        onPermissionResult = onResult

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // ตรวจสอบว่า permission ได้รับอนุญาตแล้วหรือไม่
        val allPermissionsGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            onResult(true)
        } else {
            permissionLauncher.launch(permissions)
        }
    }
}