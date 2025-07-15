// File: composeApp/src/iosMain/kotlin/com/jetbrains/kmpapp/MainViewController.kt
package com.jetbrains.kmpapp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val viewController = ComposeUIViewController {
        // สร้าง ImagePicker สำหรับ iOS
        val imagePicker = IOSImagePicker(viewController = getCurrentViewController())

        App(imagePicker = imagePicker)
    }

    return viewController
}

// Helper function เพื่อรับ current view controller
private fun getCurrentViewController(): platform.UIKit.UIViewController {
    // ใช้ static reference หรือ singleton pattern
    return IOSViewControllerProvider.currentViewController
        ?: throw IllegalStateException("No current view controller available")
}

// Singleton สำหรับเก็บ reference ของ current view controller
object IOSViewControllerProvider {
    var currentViewController: platform.UIKit.UIViewController? = null
        private set

    fun setCurrentViewController(viewController: platform.UIKit.UIViewController) {
        currentViewController = viewController
    }
}