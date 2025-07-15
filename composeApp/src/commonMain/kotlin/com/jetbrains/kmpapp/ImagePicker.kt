package com.jetbrains.kmpapp

interface ImagePicker {
    fun pickFromGallery(onResult: (String?) -> Unit)
    fun takePhoto(onResult: (String?) -> Unit)
}