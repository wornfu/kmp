/* File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/pdf/PdfViewModel.kt */
package com.jetbrains.kmpapp.pdf

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PdfViewModel {
    private val _currentDocumentIndex = MutableStateFlow(0)
    val currentDocumentIndex: StateFlow<Int> = _currentDocumentIndex

    private val _pdfState = MutableStateFlow(PdfState())
    val pdfState: StateFlow<PdfState> = _pdfState

    private val _showControls = MutableStateFlow(true)
    val showControls: StateFlow<Boolean> = _showControls

    private val _topicId = MutableStateFlow("")
    val topicId: StateFlow<String> = _topicId

    private val _pdfTrigger = MutableStateFlow(0)
    val pdfTrigger: StateFlow<Int> = _pdfTrigger

    private val _sourceType = MutableStateFlow("normal") // normal, history
    val sourceType: StateFlow<String> = _sourceType

    /**
     * แมพ Topic ID กับ PDF files แบบง่ายๆ
     */
    private fun getPdfListForTopic(topicId: String): List<String> {
        return when (topicId) {
            "1" -> listOf("1")
            "2" -> listOf("4")
            "3" -> listOf("1")
            "4" -> listOf("3", "4", "5")
            "5" -> listOf("6")
            "6" -> listOf("1", "2", "3")
            "7" -> listOf("4", "5", "6")
            "8" -> listOf("1")
            "9" -> listOf("3", "4", "5")
            "10" -> listOf("6")
            "11" -> listOf("1")
            "12" -> listOf("3", "4", "5")
            "13" -> listOf("6")
            else -> listOf("1")
        }
    }

    /**
     * รายชื่อ PDF IDs สำหรับหัวข้อปัจจุบัน
     */
    private val _documentNames = MutableStateFlow<List<String>>(emptyList())
    val documentNames: StateFlow<List<String>> = _documentNames

    /**
     * ชื่อ PDF ปัจจุบันที่กำลังแสดง
     */
    val currentDocumentName: String
        get() = _documentNames.value.getOrNull(_currentDocumentIndex.value).orEmpty()

    /**
     * เช็คว่ามี PDF หลายไฟล์หรือไม่
     */
    val hasMultiplePdfs: Boolean
        get() = _documentNames.value.size > 1

    /**
     * เริ่มต้น PDF จาก Topic ID - เริ่มที่ PDF สุดท้ายเสมอ
     */
    fun initializePdf(topicId: String, sourceType: String = "normal") {
        _topicId.value = topicId
        _sourceType.value = sourceType

        val pdfList = getPdfListForTopic(topicId)
        _documentNames.value = pdfList

        // เริ่มที่ PDF สุดท้าย (index สุดท้าย)
        _currentDocumentIndex.value = if (pdfList.isNotEmpty()) {
            if (pdfList.size == 1) {
                0  // ถ้ามีไฟล์เดียว เริ่มที่ index 0
            } else {
                pdfList.size - 1  // ถ้ามีหลายไฟล์ เริ่มที่ไฟล์สุดท้าย
            }
        } else {
            0
        }

        // รีเซ็ต PDF state
        _pdfState.value = PdfState()
        _pdfTrigger.value = _pdfTrigger.value + 1 // สำคัญ: trigger การรีเฟรช

    }

    /**
     * ดึง path ของ PDF ปัจจุบัน
     */
    fun getCurrentPdfPath(): String {
        val fileName = _documentNames.value.getOrNull(_currentDocumentIndex.value).orEmpty()
        return if (fileName.isNotEmpty()) {
            "pdfs/${fileName}.pdf"
        } else {
            ""
        }
    }

    fun updatePdfState(state: PdfState) {
        _pdfState.value = state
    }

    fun onPageChange(page: Int) {
        _pdfState.value = _pdfState.value.copy(currentPage = page)
    }

    fun zoomIn() = _pdfState.update { it.copy(zoomLevel = it.zoomLevel * 1.1f) }
    fun zoomOut() = _pdfState.update { it.copy(zoomLevel = it.zoomLevel / 1.1f) }
    fun resetZoom() = _pdfState.update { it.copy(zoomLevel = 1f) }

    val canGoPreviousPage: Boolean
        get() = _pdfState.value.currentPage > 0
    val canGoNextPage: Boolean
        get() = _pdfState.value.currentPage < _pdfState.value.totalPages - 1

    fun previousPage() {
        if (canGoPreviousPage) onPageChange(_pdfState.value.currentPage - 1)
    }

    fun nextPage() {
        if (canGoNextPage) onPageChange(_pdfState.value.currentPage + 1)
    }

    fun toggleControls() = _showControls.update { !it }

    val canNavigatePrevious: Boolean
        get() = _currentDocumentIndex.value > 0
    val canNavigateNext: Boolean
        get() = _currentDocumentIndex.value < _documentNames.value.size - 1

    fun previousDocument() {


        if (canNavigatePrevious) {
            _currentDocumentIndex.value--
            _pdfState.value = PdfState() // รีเซ็ต state
            _pdfTrigger.value = _pdfTrigger.value + 1 // สำคัญ: trigger รีเฟรช
        }
    }

    fun nextDocument() {

        if (canNavigateNext) {
            _currentDocumentIndex.value++
            _pdfState.value = PdfState() // รีเซ็ต state
            _pdfTrigger.value = _pdfTrigger.value + 1 // สำคัญ: trigger รีเฟรช

        }
    }

    /**
     * ไปยัง PDF แรกสุด
     */
    fun goToFirstDocument() {
        if (_documentNames.value.isNotEmpty()) {
            _currentDocumentIndex.value = 0
            _pdfState.value = PdfState()
        }
    }

    /**
     * ไปยัง PDF สุดท้าย
     */
    fun goToLastDocument() {
        if (_documentNames.value.isNotEmpty()) {
            _currentDocumentIndex.value = _documentNames.value.size - 1
            _pdfState.value = PdfState()
        }
    }
}