package com.jetbrains.kmpapp.pdf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.PDFKit.PDFView
import platform.PDFKit.PDFDocument
import platform.Foundation.NSURL

@Composable
actual fun UnifiedPdfViewer(
    pdfName: String,
    config: PdfConfig,
    onStateChange: (PdfState) -> Unit,
    onPageChange: (Int) -> Unit,
    modifier: Modifier
) {
    UIKitView(modifier = modifier) {
        val pdfView = PDFView().apply {
            document  = PDFDocument(NSURL.fileURLWithPath(pdfName))
            autoScales = config.fitWidth
            // ฟังชั่น delegate เพื่อ onPageChanged, zoomChanged ฯลฯ
        }
        pdfView
    }
}
