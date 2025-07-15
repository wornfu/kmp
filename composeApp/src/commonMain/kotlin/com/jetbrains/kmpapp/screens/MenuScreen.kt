// File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/screens/menu/MenuScreen.kt
package com.jetbrains.kmpapp.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main
import ismartaudit.composeapp.generated.resources.chevron_righ
import ismartaudit.composeapp.generated.resources.icon_document
import ismartaudit.composeapp.generated.resources.notify
import ismartaudit.composeapp.generated.resources.person

// 1) เพิ่มฟิลด์ formCode ใน data class
data class MenuItem(
    val title: String,
    val formCode: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    notificationCount: Int,
    onItemClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    // 2) สร้าง list ของเมนู พร้อม formCode
    val menuItems = listOf(
        MenuItem("แบบคำขอ ภส. 05-01",     "PS28"),
        MenuItem("แบบรายการขอยกเว้นเงินนำร่องทุน", "SSO105"),
        MenuItem("แบบคำขอ ภส. 05-01 (ก)", "PS28A"),
        MenuItem("แบบคำขอ ภส. 05-01/1 (ก)", "KEEP28A")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .zIndex(1f)
        ) {
            Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ยกเว้น ลดหย่อน คืนภาษี",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    "สำหรับสินค้าที่ส่งออก",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically ,
                horizontalArrangement = Arrangement.spacedBy(4.dp)

            ) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge {
                                Text(
                                    text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                ) {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            painter = painterResource(Res.drawable.notify),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                IconButton(onClick = onProfileClick) {
                    Image(
                        painter = painterResource(Res.drawable.person),
                        contentDescription = "Profile",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 3) ใส่ bg_main เป็นพื้นหลัง แล้ว overlay LazyColumn
        Box(modifier = Modifier.weight(1f)) {
            Image(
                painter = painterResource(Res.drawable.bg_main),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(menuItems) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            // 4) คลิกส่งกลับทั้ง item ที่มี .formCode
                            .clickable { onItemClick(item.formCode) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_document),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                painter = painterResource(Res.drawable.chevron_righ),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
