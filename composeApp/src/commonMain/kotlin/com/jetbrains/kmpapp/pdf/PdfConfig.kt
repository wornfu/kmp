package com.jetbrains.kmpapp.pdf

data class PdfConfig(
    val enableSwipe: Boolean = true,
    val enableZoom: Boolean = true,
    val enableDoubleTap: Boolean = true,
    val fitWidth: Boolean = false,
    val pageSpacing: Int = 0
)