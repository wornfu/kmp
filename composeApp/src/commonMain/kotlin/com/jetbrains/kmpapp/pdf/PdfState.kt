package com.jetbrains.kmpapp.pdf

data class PdfState(
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val zoomLevel: Float = 1f,
    val isReady: Boolean = false
)