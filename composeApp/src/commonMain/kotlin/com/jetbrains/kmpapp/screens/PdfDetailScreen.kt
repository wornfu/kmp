// File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/screens/pdf/PdfDetailScreen.kt
package com.jetbrains.kmpapp.screens.pdf

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetbrains.kmpapp.pdf.PdfConfig
import com.jetbrains.kmpapp.pdf.PdfState
import com.jetbrains.kmpapp.pdf.PdfViewModel
import com.jetbrains.kmpapp.pdf.UnifiedPdfViewer
import com.jetbrains.kmpapp.utils.rememberViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PdfDetailScreen(
    id: String, // Topic ID
    sourceType: String = "normal", // normal, history
    onBack: () -> Unit,
    onNext: (String) -> Unit // เปลี่ยนเป็น callback ที่รับ sourceType
) {
    val haptic = LocalHapticFeedback.current
    val viewModel = rememberViewModel { PdfViewModel() }
    val pdfTrigger by viewModel.pdfTrigger.collectAsState()

    // Collect StateFlows
    val showControls by viewModel.showControls.collectAsState()
    val pdfState by viewModel.pdfState.collectAsState()
    val documentIndex by viewModel.currentDocumentIndex.collectAsState()
    val topicId by viewModel.topicId.collectAsState()
    val currentSourceType by viewModel.sourceType.collectAsState()
    val documentNames by viewModel.documentNames.collectAsState()

    // ดึงค่าจาก ViewModel โดยตรง
    val currentDocumentName = viewModel.currentDocumentName
    val hasMultiplePdfs = viewModel.hasMultiplePdfs

    // สำหรับ PDF path ใช้ State เพื่อ trigger recomposition
    var currentPdfPath by remember { mutableStateOf("") }

    // Update path เมื่อ documentNames หรือ index เปลี่ยน
    LaunchedEffect(documentNames, documentIndex, pdfTrigger) {
        val newPath = viewModel.getCurrentPdfPath()
        currentPdfPath = newPath
    }

    // เริ่มต้น PDF เมื่อ topic ID เปลี่ยน
    LaunchedEffect(id) {
        viewModel.initializePdf(id, sourceType)
    }

    // Timeout handling
    var showTimeout by remember { mutableStateOf(false) }
    LaunchedEffect(currentPdfPath) {
        if (currentPdfPath.isNotEmpty()) {
            showTimeout = false
            delay(5000) // รอ 5 วินาที
            if (!pdfState.isReady) {
                showTimeout = true
            }
        }
    }

    Scaffold(
        topBar = {
            PdfTopBar(
                topicId = topicId,
                onBack = onBack,
                pdfState = pdfState,
                onResetZoom = viewModel::resetZoom
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                when {
                    // กรณีมาจากประวัติ - แสดง 3 ปุ่ม
                    currentSourceType == "history" -> {
                        HistoryBottomControls(
                            onNext = { onNext("single") }
                        )
                    }
                    // กรณี PDF เดียว - แสดงปุ่มเดียว
                    !hasMultiplePdfs -> {
                        SinglePdfBottomControls(
                            onNext = { onNext("single") }
                        )
                    }
                    // กรณี PDF หลายไฟล์ - แสดงปุ่มสลับ + ปุ่มดำเนินการ
                    else -> {
                        MultiplePdfBottomControls(
                            viewModel = viewModel,
                            onNext = { onNext("multiple") },
                            pdfState = pdfState,
                            currentDocumentName = currentDocumentName,
                            documentNames = documentNames,
                            currentDocumentIndex = documentIndex
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // ตรวจสอบว่าไม่มี PDF path
                currentPdfPath.isEmpty() -> {
                    // แสดง Loading
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("กำลังโหลดเอกสาร...")
                            Text("หัวข้อ: $topicId", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                showTimeout -> {
                    // แสดง Error หาก timeout
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("ไม่สามารถโหลดเอกสารได้", color = Color.Red)
                            Text("Path: $currentPdfPath", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    showTimeout = false
                                    viewModel.initializePdf(topicId, sourceType)
                                }
                            ) {
                                Text("ลองใหม่")
                            }
                        }
                    }
                }
                else -> {
                    // PDF Viewer - ใช้ path จาก StateFlow โดยตรง
                    UnifiedPdfViewer(
                        pdfName = currentPdfPath,
                        config = PdfConfig(
                            enableSwipe = true,
                            enableZoom = true,
                            enableDoubleTap = true,
                            fitWidth = true,
                            pageSpacing = 10
                        ),
                        onStateChange = viewModel::updatePdfState,
                        onPageChange = viewModel::onPageChange,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleControls()
                            }
                    )
                }
            }

            // Document indicator (top-right) - แสดงเฉพาะกรณีมี PDF หลายไฟล์
            if (hasMultiplePdfs && showControls) {
                AnimatedVisibility(
                    visible = showControls,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    DocumentIndicator(
                        currentIndex = documentIndex,
                        totalDocs = documentNames.size,
                        currentDocumentName = currentDocumentName
                    )
                }
            }

            // Floating zoom controls (right side)
            if (showControls && pdfState.isReady) {
                AnimatedVisibility(
                    visible = true,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp),
                    enter = slideInHorizontally { it },
                    exit = slideOutHorizontally { it }
                ) {
                    ZoomControls(
                        zoomLevel = pdfState.zoomLevel,
                        onZoomIn = viewModel::zoomIn,
                        onZoomOut = viewModel::zoomOut
                    )
                }
            }

            // Page navigation (left side)
            if (showControls && pdfState.isReady && pdfState.totalPages > 1) {
                AnimatedVisibility(
                    visible = true,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp),
                    enter = slideInHorizontally { -it },
                    exit = slideOutHorizontally { -it }
                ) {
                    PageControls(
                        currentPage = pdfState.currentPage,
                        totalPages = pdfState.totalPages,
                        onPrevious = viewModel::previousPage,
                        onNext = viewModel::nextPage,
                        canPrevious = pdfState.currentPage > 0,
                        canNext = pdfState.currentPage < pdfState.totalPages - 1
                    )
                }
            }
        }
    }
}

// Top Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PdfTopBar(
    topicId: String,
    onBack: () -> Unit,
    pdfState: PdfState,
    onResetZoom: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "รายละเอียดคำขอ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (pdfState.totalPages > 0) {
                    Text(
                        text = "หน้า ${pdfState.currentPage + 1}/${pdfState.totalPages}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "ย้อนกลับ", tint = Color.Black)
            }
        },
        actions = {
            if (pdfState.isReady) {
                IconButton(onClick = onResetZoom) {
                    Icon(Icons.Default.CenterFocusStrong, contentDescription = "รีเซ็ตซูม", tint = Color.Black)
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    )
}

// Document Indicator
@Composable
private fun DocumentIndicator(
    currentIndex: Int,
    totalDocs: Int,
    currentDocumentName: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${currentIndex + 1}/$totalDocs",
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Zoom Controls
@Composable
private fun ZoomControls(
    zoomLevel: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onZoomIn, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ZoomIn, contentDescription = "ซูมเข้า")
            }
            Text(
                text = "${(zoomLevel * 100).toInt()}%",
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            IconButton(onClick = onZoomOut, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ZoomOut, contentDescription = "ซูมออก")
            }
        }
    }
}

// Page Controls
@Composable
private fun PageControls(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    canPrevious: Boolean,
    canNext: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onPrevious, enabled = canPrevious, modifier = Modifier.size(48.dp)) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = "หน้าก่อนหน้า",
                    tint = if (canPrevious) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
            IconButton(onClick = onNext, enabled = canNext, modifier = Modifier.size(48.dp)) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "หน้าถัดไป",
                    tint = if (canNext) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

// Bottom Controls สำหรับ PDF หลายไฟล์
@Composable
private fun MultiplePdfBottomControls(
    viewModel: PdfViewModel,
    onNext: () -> Unit,
    pdfState: PdfState,
    currentDocumentName: String,
    documentNames: List<String>,
    currentDocumentIndex: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp) // ไม่มีระยะห่าง เพื่อให้ดูเป็นกล่องเดียว
            ) {
                // ปุ่มซ้าย - โค้งมนด้านซ้าย (ด้านขวาตรง)
                Card(
                    modifier = Modifier
                        .size(50.dp),
                    shape = RoundedCornerShape(
                        topStart = 25.dp,      // โค้งมนด้านซ้ายบน
                        topEnd = 0.dp,         // ด้านขวาบนตรง
                        bottomStart = 25.dp,   // โค้งมนด้านซ้ายล่าง
                        bottomEnd = 0.dp       // ด้านขวาล่างตรง
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentDocumentIndex > 0)
                            Color(0xFF4A90E2)
                        else
                            Color(0xFF9E9E9E)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (currentDocumentIndex > 0) 6.dp else 2.dp
                    )
                ) {
                    IconButton(
                        onClick = viewModel::previousDocument,
                        enabled = currentDocumentIndex > 0,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "เอกสารก่อนหน้า",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))

                // ปุ่มกลาง - สี่เหลี่ยมผืนผ้าปกติ (ไม่มีมุมโค้ง)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(0.dp), // ไม่มีมุมโค้งเลย
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onNext() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "ไปหน้าตรวจสอบสินค้า",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))

                // ปุ่มขวา - โค้งมนด้านขวา (ด้านซ้ายตรง)
                Card(
                    modifier = Modifier
                        .size(50.dp),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,       // ด้านซ้ายบนตรง
                        topEnd = 25.dp,        // โค้งมนด้านขวาบน
                        bottomStart = 0.dp,    // ด้านซ้ายล่างตรง
                        bottomEnd = 25.dp      // โค้งมนด้านขวาล่าง
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentDocumentIndex < documentNames.size - 1)
                            Color(0xFF4A90E2)
                        else
                            Color(0xFF9E9E9E)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (currentDocumentIndex < documentNames.size - 1) 6.dp else 2.dp
                    )
                ) {
                    IconButton(
                        onClick = viewModel::nextDocument,
                        enabled = currentDocumentIndex < documentNames.size - 1,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "เอกสารถัดไป",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Bottom Controls สำหรับ PDF เดียว
@Composable
private fun SinglePdfBottomControls(
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(
                    "ไปหน้าลงลายมือชื่อ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // เพิ่มช่องว่างด้านล่าง
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// Bottom Controls สำหรับมาจากประวัติ (3 ปุ่ม)
@Composable
private fun HistoryBottomControls(
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(
                    "ไปหน้าตรวจสอบสินค้า",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // เพิ่มช่องว่างด้านล่าง
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}