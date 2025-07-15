package com.jetbrains.kmpapp.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import com.rajat.pdfviewer.HeaderData
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.util.PdfSource
@Composable
actual fun UnifiedPdfViewer(
    pdfName: String,
    config: PdfConfig,
    onStateChange: (PdfState) -> Unit,
    onPageChange: (Int) -> Unit,
    modifier: Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // สร้าง PdfSource ใหม่ทุกครั้งที่ pdfName เปลี่ยน
    val pdfSource = remember(pdfName) {
        PdfSource.PdfSourceFromAsset(pdfName)
    }

    // สร้าง callback objects ใหม่
    val statusCallBack = remember(pdfName) { // เพิ่ม pdfName เป็น key
        object : PdfRendererView.StatusCallBack {
            override fun onPdfLoadSuccess(path: String) {
                onStateChange(PdfState(isReady = true))
            }

            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                onPageChange(currentPage)
                onStateChange(PdfState(
                    currentPage = currentPage,
                    totalPages = totalPage,
                    isReady = true
                ))
            }

            override fun onError(throwable: Throwable) {
                throwable.printStackTrace()
                onStateChange(PdfState(isReady = false))
            }
        }
    }

    val zoomListener = remember(pdfName) { // เพิ่ม pdfName เป็น key
        object : PdfRendererView.ZoomListener {
            override fun onZoomChanged(isZoomedIn: Boolean, scale: Float) {
                onStateChange(PdfState(zoomLevel = scale, isReady = true))
            }
        }
    }

    // LaunchedEffect จะ trigger ทุกครั้งที่ pdfName เปลี่ยน
    LaunchedEffect(pdfName) {
        // รีเซ็ต state ก่อนโหลดไฟล์ใหม่
        onStateChange(PdfState(isReady = false))
    }

    // เพิ่ม key สำหรับ PdfRendererViewCompose เพื่อบังคับ recomposition
    key(pdfName) {
        PdfRendererViewCompose(
            source = pdfSource,
            lifecycleOwner = lifecycleOwner,
            modifier = modifier,
            headers = HeaderData(emptyMap()),
            statusCallBack = statusCallBack,
            zoomListener = zoomListener
        )
    }
}