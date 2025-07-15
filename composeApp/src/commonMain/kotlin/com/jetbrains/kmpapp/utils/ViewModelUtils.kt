// File: commonMain/kotlin/com/jetbrains/kmpapp/utils/ViewModelUtils.kt
package com.jetbrains.kmpapp.utils

import androidx.compose.runtime.*

/**
 * Simple helper to remember a ViewModel instance in Compose
 */
@Composable
inline fun <VM> rememberViewModel(crossinline creator: () -> VM): VM {
    return remember { creator() }
}

