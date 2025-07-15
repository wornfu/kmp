package com.jetbrains.kmpapp.screens.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.FilterList
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
import com.jetbrains.kmpapp.screens.filter.FilterData

/**
 * Model สำหรับรายการประวัติ
 */
data class HistoryItem(
    val id: String,
    val companyName: String,
    val receiptNumber: String,
    val receivedDate: String,
    val status: String,
    val sourceLocation: String = "",
    val destinationLocation: String = ""
)

/**
 * Model สำหรับ Filter
 */
data class HistoryFilter(
    val startDate: String? = null,
    val endDate: String? = null,
    val sourceLocation: String? = null,
    val destinationLocation: String? = null
)

/**
 * ฟังก์ชันเช็คว่าวันที่อยู่ในช่วงที่กำหนดไหม (Mock implementation)
 */
private fun isDateInRange(dateStr: String, startDate: String, endDate: String): Boolean {
    // Mock implementation - ในความเป็นจริงต้องแปลงเป็น Date object เปรียบเทียบ
    return true // ให้ผ่านไปก่อน
}

// Mock ข้อมูล
private val mockHistoryItems = listOf(
    HistoryItem("1", "บริษัท ชาน ดีนา เอ็นเตอร์ไพรส์ (ประเทศไทย) จำกัด", "6800304004934", "30 พ.ย. 2568", "รอเจ้าหน้าที่ลงความเห็น", "กรุงเทพ", "เชียงใหม่"),
    HistoryItem("2", "บริษัท คลอสบอส บริวว่อง (ประเทศไทย) จำกัด", "6801010404640", "30 พ.ย. 2568", "รอเจ้าหน้าที่ลงความเห็น", "กรุงเทพ", "ขอนแก่น"),
    HistoryItem("3", "บริษัท ยูพี-คาเอสซีเอ (ประเทศไทย) จำกัด", "6807060003778", "29 พ.ย. 2568", "รอเจ้าหน้าที่ลงความเห็น", "กรุงเทพ", "ภูเก็ต"),
    HistoryItem("4", "บริษัท ฟิเอส.พี.เอส.บียอยด์ จำกัด (มหาชน)", "6807060003709", "29 พ.ย. 2568", "รอเจ้าหน้าที่ลงความเห็น", "กรุงเทพ", "เชียงราย"),
    HistoryItem("5", "บริษัท ฟิเอส.พี.เอส.บียอยด์ จำกัด (มหาชน)", "6807060003708", "29 พ.ย. 2568", "อนุมัติ", "กรุงเทพ", "สุราษฎร์ธานี")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryListScreen(
    currentFilter: FilterData = FilterData(),
    onBack: () -> Unit,
    onItemClick: (HistoryItem) -> Unit,
    onFilterClick: () -> Unit
) {
    // State สำหรับ search
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // ใช้ currentFilter ที่ส่งมาจาก navigation
    val appliedFilter = currentFilter

    // Filter ข้อมูล
    val filteredItems = mockHistoryItems.filter { item ->
        val matchesSearch = searchQuery.text.isEmpty() ||
                item.receiptNumber.contains(searchQuery.text, ignoreCase = true) ||
                item.companyName.contains(searchQuery.text, ignoreCase = true)

        val sourceLocation = appliedFilter.sourceLocation
        val destinationLocation = appliedFilter.destinationLocation
        val startDate = appliedFilter.startDate
        val endDate = appliedFilter.endDate

        val matchesFilter = (sourceLocation.isEmpty() ||
                item.sourceLocation.contains(sourceLocation, ignoreCase = true)) &&
                (destinationLocation.isEmpty() ||
                        item.destinationLocation.contains(destinationLocation, ignoreCase = true)) &&
                (startDate.isEmpty() || endDate.isEmpty() ||
                        isDateInRange(item.receivedDate, startDate, endDate))

        matchesSearch && matchesFilter
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
                            text = "ประวัติรายการย้อนหลัง 30 วัน",
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

                    // Search Bar with Filter Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
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
                                .weight(1f)
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

                        // Filter Button
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .size(56.dp)
                                .clickable { onFilterClick() }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Results Count Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${filteredItems.size} รายการ",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )

                            // แสดง Filter Status
                            if (appliedFilter.startDate.isNotEmpty() && appliedFilter.endDate.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "กรองแล้ว",
                                        color = Color(0xFF4CAF50),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } else if (appliedFilter.locationCategory != "ดันทัง") {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF2196F3).copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = appliedFilter.locationCategory,
                                        color = Color(0xFF2196F3),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // History List
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredItems) { item ->
                            HistoryItemCard(
                                item = item,
                                onClick = { onItemClick(item) }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun HistoryItemCard(
    item: HistoryItem,
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
                text = item.companyName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "เลขที่รับ: ${item.receiptNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            Text(
                text = "วันที่รับเรื่อง: ${item.receivedDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            // แสดงต้นทาง-ปลายทาง (ถ้ามี)
            if (item.sourceLocation.isNotEmpty() && item.destinationLocation.isNotEmpty()) {
                Text(
                    text = "เส้นทาง: ${item.sourceLocation} → ${item.destinationLocation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(8.dp))

            // Status Badge
            val isPending = item.status.startsWith("รอ")
            val statusColor = if (isPending) Color(0xFFFF9800) else Color(0xFF43A047)

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "สถานะ: ${item.status}",
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