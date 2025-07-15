package com.jetbrains.kmpapp.screens.pdf

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main
import ismartaudit.composeapp.generated.resources.chevron_left

/**
 * Model สำหรับรายการ PDF
 */
data class PdfItem(
    val id: String,
    val companyName: String,
    val receiptNumber: String,
    val receivedDate: String,
    val status: String
)

// Mock ข้อมูลแยกตาม sourceType
private fun getMockPdfItems(sourceType: String): List<PdfItem> {
    return when (sourceType) {
        "approval" -> listOf(
            PdfItem("1", "บริษัท เซลส์แห่งประเทศไทย จำกัด", "6510030031836", "26 ส.ค. 2565", "รอเจ้าหน้าที่ลงความเห็น"),
            PdfItem("2", "บริษัท วายสิน (ประเทศไทย) จำกัด", "63100300100224", "16 มี.ค. 2563", "รอเจ้าหน้าที่ลงความเห็น"),
            PdfItem("3", "บริษัท บางจาก คอร์ปอเรชั่น จำกัด (มหาชน)", "524/2563", "21 พ.ย. 2562", "รอเจ้าหน้าที่ลงความเห็น")
        )
        "before_export" -> listOf(
            PdfItem("4", "บริษัท ไทยออยล์ จำกัด (มหาชน)", "BE001/2568", "01 ธ.ค. 2568", "รอตรวจสอบ"),
            PdfItem("5", "บริษัท บางจาก คอร์ปอเรชั่น จำกัด (มหาชน)", "BE002/2568", "02 ธ.ค. 2568", "อนุมัติ"),
            PdfItem("6", "บริษัท ปตท. สำรวจและผลิตปิโตรเลียม จำกัด (มหาชน)", "BE003/2568", "03 ธ.ค. 2568", "รอตรวจสอบ")
        )
        "export_oil" -> listOf(
            PdfItem("7", "บริษัท เชลล์ แห่งประเทศไทย จำกัด", "OIL001/2568", "05 ธ.ค. 2568", "รอเจ้าหน้าที่ลงความเห็น"),
            PdfItem("8", "บริษัท เอสโซ่ (ประเทศไทย) จำกัด (มหาชน)", "OIL002/2568", "06 ธ.ค. 2568", "อนุมัติ"),
            PdfItem("9", "บริษัท คาลเท็กซ์ (ประเทศไทย) จำกัด", "OIL003/2568", "07 ธ.ค. 2568", "รอเจ้าหน้าที่ลงความเห็น")
        )
        "export_alcohol" -> listOf(
            PdfItem("10", "บริษัท ไทยเบฟเวอเรจ จำกัด (มหาชน)", "ALC001/2568", "10 ธ.ค. 2568", "รอเจ้าหน้าที่ลงความเห็น"),
            PdfItem("11", "บริษัท เซิร์ปเฟส (ประเทศไทย) จำกัด", "ALC002/2568", "11 ธ.ค. 2568", "อนุมัติ"),
            PdfItem("12", "บริษัท แสงโสม จำกัด (มหาชน)", "ALC003/2568", "12 ธ.ค. 2568", "รอเจ้าหน้าที่ลงความเห็น")
        )
        else -> listOf(
            PdfItem("13", "บริษัท ตัวอย่าง จำกัด", "DEFAULT001", "01 ธ.ค. 2568", "รอเจ้าหน้าที่ลงความเห็น")
        )
    }
}

// กำหนด title ตาม sourceType
private fun getTitleBySourceType(sourceType: String): String {
    return when (sourceType) {
        "approval" -> "การพิจารณาอนุมัติยกเว้นภาษี"
        "before_export" -> "ก่อนนำออกจากโรงงาน (ต้นทาง)"
        "export_oil" -> "ก่อนส่งออกนอกราชอาณาจักร (สำหรับสินค้าน้ำมัน)"
        "export_alcohol" -> "ก่อนส่งออกนอกราชอาณาจักร (สำหรับสินค้าสุราและยาสูบ)"
        else -> "รายละเอียดคำขอ"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfListScreen(
    sourceType: String,
    onBack: () -> Unit,
    onItemClick: (PdfItem) -> Unit
) {
    // State สำหรับ search
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // ดึงข้อมูล PDF ตาม sourceType
    val pdfItems = remember(sourceType) { getMockPdfItems(sourceType) }

    // กำหนด title ตาม sourceType
    val title = getTitleBySourceType(sourceType)

    // Filter ข้อมูล
    val filteredItems = pdfItems.filter { pdf ->
        val query = searchQuery.text
        query.isEmpty() ||
                pdf.receiptNumber.contains(query, ignoreCase = true) ||
                pdf.companyName.contains(query, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ภส. 05-01",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.chevron_left),
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Background stripes
                Image(
                    painter = painterResource(Res.drawable.bg_main),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(Modifier.height(12.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue
                        },
                        placeholder = {
                            Text(
                                "เลขที่รับ, ชื่อบริษัท, วันที่/เดือน/ปี พ.ศ.",
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        singleLine = true,
                        enabled = true,
                        readOnly = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor      = Color.White,
                            unfocusedContainerColor    = Color.White,
                            focusedIndicatorColor      = Color.Transparent,
                            unfocusedIndicatorColor    = Color.Transparent,
                            focusedLeadingIconColor    = Color.Gray,
                            unfocusedLeadingIconColor  = Color.Gray,
                            focusedPlaceholderColor    = Color.Gray,
                            unfocusedPlaceholderColor  = Color.Gray,
                            focusedTextColor           = Color.Black,
                            unfocusedTextColor         = Color.Black,
                            cursorColor                = Color.Black,
                            disabledContainerColor     = Color.White,
                            disabledTextColor          = Color.Black
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    // Results Count Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${filteredItems.size} รายการ",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.Black
                                )

                                Spacer(Modifier.weight(1f))

                                // แสดง source type indicator
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF2196F3).copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = when (sourceType) {
                                            "approval" -> "อนุมัติ"
                                            "before_export" -> "ก่อนออก"
                                            "export_oil" -> "ส่งออก (น้ำมัน)"
                                            "export_alcohol" -> "ส่งออก (สุรา)"
                                            else -> sourceType
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2196F3),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // PDF List
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredItems) { pdf ->
                            PdfItemCard(
                                pdf = pdf,
                                onClick = { onItemClick(pdf) }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun PdfItemCard(
    pdf: PdfItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = pdf.companyName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "เลขที่รับ: ${pdf.receiptNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            Text(
                text = "วันที่รับเรื่อง: ${pdf.receivedDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            Spacer(Modifier.height(8.dp))

            // Status Badge
            val isPending = pdf.status.startsWith("รอ")
            val statusColor = if (isPending) Color(0xFFFF9800) else Color(0xFF43A047)

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "สถานะ: ${pdf.status}",
                    color = statusColor,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}