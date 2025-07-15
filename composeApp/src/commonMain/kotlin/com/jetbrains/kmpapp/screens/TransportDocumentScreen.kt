package com.jetbrains.kmpapp.screens.transport

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main

// Data model สำหรับรายการใบขนย้าย
data class TransportDocumentItem(
    val id: String,
    val setNumber: String,
    val serviceCode: String,
    val description: String
)

// Mock data สำหรับรายการใบขนย้าย
private fun getTransportDocuments(topicId: String): List<TransportDocumentItem> {
    return listOf(
        TransportDocumentItem(
            id = "1",
            setNumber = "ชุดที่ 1",
            serviceCode = "DPC101",
            description = "ใบขนย้ายสุรา ชุดที่ 1"
        ),
        TransportDocumentItem(
            id = "2",
            setNumber = "ชุดที่ 2",
            serviceCode = "DPC101",
            description = "ใบขนย้ายสุรา ชุดที่ 2"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportDocumentScreen(
    topicId: String,
    sourceType: String,
    onBack: () -> Unit,
    onDocumentClick: (TransportDocumentItem) -> Unit
) {
    val documents = remember(topicId) { getTransportDocuments(topicId) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "ใบขนย้ายสุรา",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // รายการใบขนย้าย
                documents.forEach { document ->
                    TransportDocumentCard(
                        document = document,
                        onClick = { onDocumentClick(document) }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

            }
        }
    }
}

@Composable
private fun TransportDocumentCard(
    document: TransportDocumentItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = document.setNumber,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}