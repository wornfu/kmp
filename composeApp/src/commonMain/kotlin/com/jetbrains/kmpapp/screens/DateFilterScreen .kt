package com.jetbrains.kmpapp.screens.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main
import ismartaudit.composeapp.generated.resources.chevron_left

/**
 * Model สำหรับข้อมูล Filter
 */
data class FilterData(
    val startDate: String = "",
    val endDate: String = "",
    val sourceLocation: String = "",
    val destinationLocation: String = "",
    val locationCategory: String = "ดันทัง" // ประเภทสถานที่
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFilterScreen(
    currentFilter: FilterData = FilterData(),
    onBack: () -> Unit,
    onApplyFilter: (FilterData) -> Unit,
    onClearFilter: () -> Unit
) {
    var filterData by remember { mutableStateOf(currentFilter) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showLocationDropdown by remember { mutableStateOf(false) }

    // Mock location categories
    val locationCategories = listOf("ดันทัง", "กรุงเทพ", "เชียงใหม่", "ขอนแก่น", "ภูเก็ต")

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        text = "ประวัติรายการย้อนหลัง 30 วัน",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
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
                // Background
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
                    Spacer(Modifier.height(20.dp))

                    // ————— ประเภท —————
                    Text(
                        text = "ประเภท",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Location Category Dropdown
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLocationDropdown = !showLocationDropdown }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = filterData.locationCategory,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    if (showLocationDropdown) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Column {
                                locationCategories.forEach { category ->
                                    Text(
                                        text = category,
                                        fontSize = 16.sp,
                                        color = Color.Black,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                filterData = filterData.copy(locationCategory = category)
                                                showLocationDropdown = false
                                            }
                                            .padding(16.dp)
                                    )
                                    if (category != locationCategories.last()) {
                                        Divider(color = Color.Gray.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // ————— วันที่ —————
                    Text(
                        text = "วันที่",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Start Date
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showStartDatePicker = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = if (filterData.startDate.isEmpty()) "เลือกวันที่เริ่มต้น" else filterData.startDate,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // End Date
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEndDatePicker = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = if (filterData.endDate.isEmpty()) "เลือกวันที่สิ้นสุด" else filterData.endDate,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // ————— ปุ่ม Action —————
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onClearFilter,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Text(
                                text = "ยกเลิกกรอง",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { onApplyFilter(filterData) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text(
                                text = "ปิดกรอง",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    )

    // Date Picker Dialogs (Mock)
    if (showStartDatePicker) {
        AlertDialog(
            onDismissRequest = { showStartDatePicker = false },
            title = { Text("เลือกวันที่เริ่มต้น") },
            text = { Text("Mock Date Picker - เลือกวันที่") },
            confirmButton = {
                TextButton(
                    onClick = {
                        filterData = filterData.copy(startDate = "01 พ.ย. 2568")
                        showStartDatePicker = false
                    }
                ) { Text("ตกลง") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("ยกเลิก")
                }
            }
        )
    }

    if (showEndDatePicker) {
        AlertDialog(
            onDismissRequest = { showEndDatePicker = false },
            title = { Text("เลือกวันที่สิ้นสุด") },
            text = { Text("Mock Date Picker - เลือกวันที่") },
            confirmButton = {
                TextButton(
                    onClick = {
                        filterData = filterData.copy(endDate = "30 พ.ย. 2568")
                        showEndDatePicker = false
                    }
                ) { Text("ตกลง") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("ยกเลิก")
                }
            }
        )
    }
}
