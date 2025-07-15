// File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/screens/notification/NotificationScreen.kt
package com.jetbrains.kmpapp.screens.notification

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main
import ismartaudit.composeapp.generated.resources.chevron_left
import ismartaudit.composeapp.generated.resources.notify
import kotlin.math.roundToInt

data class NotificationItem(
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val isNew: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    items: List<NotificationItem>,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onItemClick: (NotificationItem) -> Unit,
    onDeleteItem: (Int) -> Unit = {} // ⭐ เพิ่ม callback สำหรับลบแจ้งเตือน
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar แบบเดียวกับ MenuScreen
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ปุ่ม back
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.chevron_left),
                    contentDescription = "ย้อนกลับ",
                    modifier = Modifier.size(24.dp)
                )
            }

            // ชื่อหน้า ตรงกลาง
            Text(
                text = "ข้อความแจ้งเตือน",
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )

            // ปุ่ม settings
            IconButton(
                onClick = onSettings,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = "ตั้งค่า"
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // เนื้อหาหลัก บน bg_main
        Box(modifier = Modifier.weight(1f)) {
            // พื้นหลัง
            Image(
                painter = painterResource(Res.drawable.bg_main),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // ⭐ แสดงข้อความเมื่อไม่มีแจ้งเตือน
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.notify),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "ไม่มีข้อความแจ้งเตือน",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // ⭐ ใช้ itemsIndexed เพื่อได้ index สำหรับการลบ
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        items = items,
                        key = { index, item -> "${item.title}-${item.timestamp}-$index" }
                    ) { index, item ->
                        // ⭐ ใช้ SwipeToDeleteItem component
                        SwipeToDeleteItem(
                            item = item,
                            onItemClick = { onItemClick(item) },
                            onDelete = { onDeleteItem(index) }
                        )
                    }
                }
            }
        }
    }
}

// ⭐ Component สำหรับ Swipe to Delete - ใช้ manual gesture detection
@Composable
private fun SwipeToDeleteItem(
    item: NotificationItem,
    onItemClick: () -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isDeleting by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    // คำนวณ threshold สำหรับการลบ (หากปัดเกิน 30% ของความกว้าง)
    val deleteThreshold = with(density) { (-240).dp.toPx() }

    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            onDelete()
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // ⭐ พื้นหลังสีแดงที่จะแสดงเมื่อปัด - สร้างการ์ดเดียวกันแต่เป็นสีแดง
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Red),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // ⭐ จำลองโครงสร้างเดียวกันกับการ์ดจริงเพื่อให้มีขนาดเท่ากัน
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(12.dp)
            ) {
                // พื้นที่สำหรับไอคอนแจ้งเตือน (โปร่งใส)
                Spacer(modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(12.dp))

                // เนื้อหาตรงกลาง (โปร่งใส)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        maxLines = 1,
                        color = Color.Transparent
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        color = Color.Transparent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.timestamp,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color.Transparent
                    )
                }

                // ⭐ ไอคอนลบที่แสดงจริง
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "ลบ",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ลบ",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // พื้นที่สำหรับจุดแจ้งเตือนใหม่
                if (item.isNew) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }

        // ⭐ การ์ดหลักที่สามารถปัดได้ - วางทับบนพื้นหลังสีแดง
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX <= deleteThreshold) {
                                // ปัดเกิน threshold แล้ว -> ลบ
                                isDeleting = true
                            } else {
                                // ยังไม่เกิน threshold -> กลับคืนตำแหน่งเดิม
                                offsetX = 0f
                            }
                        }
                    ) { change, dragAmount ->
                        // อนุญาตให้ปัดซ้ายเท่านั้น
                        val newOffset = offsetX + dragAmount
                        if (newOffset <= 0) {
                            offsetX = newOffset
                        }
                    }
                }
        ) {
            NotificationItemCard(
                item = item,
                onItemClick = onItemClick
            )
        }
    }
}

// ⭐ แยก Component การ์ดแจ้งเตือนออกมา
@Composable
private fun NotificationItemCard(
    item: NotificationItem,
    onItemClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isNew) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .background(Color(0xFFBBDEFB), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.notify),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    maxLines = 1
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    maxLines = 2
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.timestamp,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                )
            }
            if (item.isNew) {
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .size(8.dp)
                        .background(Color.Red, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}