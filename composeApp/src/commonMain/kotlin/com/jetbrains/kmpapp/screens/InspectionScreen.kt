package com.jetbrains.kmpapp.screens.inspection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionScreen(
    topicId: String,
    onBack: () -> Unit,
    onContinue: (String) -> Unit
) {
    // Mocked plate number, display-only
    var plateNumber by remember { mutableStateOf("72-4716 สมุทปราการ") }
    var productName by remember { mutableStateOf("LPG") }
    var quantity by remember { mutableStateOf("15750") }
    var unit by remember { mutableStateOf("กิโลกรัม") }
    var markerValue by remember { mutableStateOf("0") }
    var dutyName by remember { mutableStateOf("") }
    var itemCount by remember { mutableStateOf("0") }
    var isExpanded by remember { mutableStateOf(true) }
    var isMainCardSelected by remember { mutableStateOf(false) }
    var isProductSelected by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(isMainCardSelected) {
        if (isMainCardSelected) {
            isProductSelected = true
            isDropdownExpanded = true
        } else {
            isProductSelected = false
        }
    }
    LaunchedEffect(isProductSelected) {
        if (!isProductSelected) {
            isMainCardSelected = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "รายการสินค้า",
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
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { onContinue(topicId) },
                    enabled = isMainCardSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Text(
                        "ไปหน้าลงรายมือชื่อ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(Res.drawable.bg_main),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "บริษัท บางจาก คอร์ปอเรชั่น จำกัด (มหาชน)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "เลขที่รับ: 68100300101191",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "วันที่รับเรื่อง: 28 พ.ค. 2568",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "จำนวน: 1 รายการ",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Checkbox(
                                checked = isMainCardSelected,
                                onCheckedChange = { checked ->
                                    isMainCardSelected = checked
                                    if (!checked) {
                                        isProductSelected = false
                                        isDropdownExpanded = false
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2196F3))
                            )
                        }
                    }
                }

                item {
                    // Form Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            // Plate Number Display and Dropdown
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "เลขทะเบียนยานพาหนะ:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Blue,
                                    modifier = Modifier.weight(1f)
                                )

                                Box(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(40.dp)
                                        .background(Color.White, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = plateNumber,
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                                        .clickable { isDropdownExpanded = !isDropdownExpanded },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        if (isDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                }
                            }

                            if (isDropdownExpanded) {
                                ProductSection(
                                    productName,
                                    isExpanded,
                                    isProductSelected,
                                    onHeaderClick = { isExpanded = !isExpanded },
                                    onProductChecked = { checked ->
                                        isProductSelected = checked
                                        if (!checked) isMainCardSelected = false
                                    },
                                    quantity,
                                    onQuantityChange = { quantity = it },
                                    unit,
                                    onUnitChange = { unit = it },
                                    markerValue,
                                    onMarkerChange = { markerValue = it },
                                    dutyName,
                                    onDutyNameChange = { dutyName = it },
                                    itemCount,
                                    onItemCountChange = { itemCount = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSection(
    productName: String,
    isExpanded: Boolean,
    isProductChecked: Boolean,
    onHeaderClick: () -> Unit,
    onProductChecked: (Boolean) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    markerValue: String,
    onMarkerChange: (String) -> Unit,
    dutyName: String,
    onDutyNameChange: (String) -> Unit,
    itemCount: String,
    onItemCountChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4FC3F7))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable { onHeaderClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ชื่อสินค้า",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = productName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = isProductChecked,
                    onCheckedChange = onProductChecked,
                    colors = CheckboxDefaults.colors(checkedColor = Color.Black, uncheckedColor = Color.Black)
                )
            }

            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                        .padding(16.dp)
                ) {
                    DetailRow("ปริมาณตั้งเบิก", quantity, onQuantityChange)
                    DetailRow("หน่วย", unit, onUnitChange)
                    DetailRow("ค่า Marker ผปก", markerValue, onMarkerChange)
                    DetailRow("ชื่อสรรพสามิต", dutyName, onDutyNameChange)
                    DetailRow("จำนวน", itemCount, onItemCountChange)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(36.dp)
                .background(Color.White, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                singleLine = true
            )
        }
    }
    Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 0.5.dp, modifier = Modifier.padding(top = 8.dp))
}
