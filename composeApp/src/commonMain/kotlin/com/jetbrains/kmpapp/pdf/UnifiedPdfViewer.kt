
/* File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/pdf/UnifiedPdfViewer.kt */
package com.jetbrains.kmpapp.pdf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Expect annotation for platform-specific PDF viewer implementation.
 */
@Composable
expect fun UnifiedPdfViewer(
    pdfName: String,
    config: PdfConfig,
    onStateChange: (PdfState) -> Unit,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
)
