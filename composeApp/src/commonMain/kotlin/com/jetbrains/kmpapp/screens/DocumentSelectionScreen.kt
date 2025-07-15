package com.jetbrains.kmpapp.screens.document

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main

// Data model สำหรับรายการเอกสาร
data class DocumentItem(
    val id: String,
    val title: String,
    val icon: String, // "document", "image", "transport"
    val hasNotification: Boolean = false,
    val notificationCount: Int = 0
)

// Mock data สำหรับรายการเอกสาร
private fun getDocumentItems(): List<DocumentItem> {
    return listOf(
        DocumentItem(
            id = "details",
            title = "รายละเอียดแนบมาด้วย",
            icon = "document",
            hasNotification = false
        ),
        DocumentItem(
            id = "images",
            title = "เลือกรูปภาพ",
            icon = "image",
            hasNotification = false
        ),
        DocumentItem(
            id = "transport",
            title = "ใบขนย้ายสุรา",
            icon = "transport",
            hasNotification = true,
            notificationCount = 1
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentSelectionScreen(
    sourceType: String,
    onBack: () -> Unit,
    onItemClick: (DocumentItem) -> Unit
) {
    val documentItems = remember { getDocumentItems() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "เลือกรายการ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "ย้อนกลับ",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background
            Image(
                painter = painterResource(Res.drawable.bg_main),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // รายการเอกสาร
                documentItems.forEach { item ->
                    DocumentItemCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentItemCard(
    item: DocumentItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White), // เปลี่ยนเป็นสีขาว
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray.copy(alpha = 0.1f), CircleShape), // เปลี่ยน background ไอคอน
                contentAlignment = Alignment.Center
            ) {
                // Icon based on type
                when (item.icon) {
                    "document" -> {
                        // เอกสาร
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF2196F3), RoundedCornerShape(4.dp))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(2.dp),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(Color.White)
                                    )
                                }
                            }
                        }
                    }
                    "image" -> {
                        // รูปภาพ
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color.White, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset((-2).dp, 2.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(Color.White)
                                    .align(Alignment.BottomCenter)
                            )
                        }
                    }
                    "transport" -> {
                        // รถขนส่ง
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFFF9800), RoundedCornerShape(4.dp))
                        ) {
                            // ตัวรถ
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(10.dp)
                                    .background(Color.White)
                                    .align(Alignment.Center)
                            )
                            // ล้อ
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 2.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(Color.White, CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black, // เปลี่ยนเป็นสีดำ
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Arrow (สร้างขึ้นเอง)
            Box(
                modifier = Modifier
                    .size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ">",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray // เปลี่ยนเป็นสีเทา
                )
            }
        }
    }
}