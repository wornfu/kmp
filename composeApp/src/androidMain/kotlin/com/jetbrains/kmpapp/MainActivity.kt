// File: composeApp/src/androidMain/kotlin/com/jetbrains/kmpapp/MainActivity.kt
package com.jetbrains.kmpapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // สร้าง ImagePicker สำหรับ Android
        val imagePicker = AndroidImagePicker(this)

        setContent {
            App(imagePicker = imagePicker)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}