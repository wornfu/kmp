package com.jetbrains.kmpapp.screens.submenu

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
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main
import ismartaudit.composeapp.generated.resources.chevron_left
import ismartaudit.composeapp.generated.resources.chevron_righ
import ismartaudit.composeapp.generated.resources.icon_document
import ismartaudit.composeapp.generated.resources.notify
import ismartaudit.composeapp.generated.resources.qr_code

/**
 * ข้อมูลเมนูย่อย (กำหนดคงที่ ไม่รับจาก App)
 */
private val submenuItems = listOf(
    SubMenuItem("opinion", "ความเห็นเจ้าหน้าที่สรรพสามิต", Res.drawable.icon_document),
    SubMenuItem("approval", "การพิจารณาอนุมัติของ ส. พื้นที่", Res.drawable.notify)
)

data class SubMenuItem(
    val id: String,
    val title: String,
    val iconRes: org.jetbrains.compose.resources.DrawableResource
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubMenuScreen(
    onBack: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // TopBar
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .zIndex(1f)
        ) {
            Icon(
                painter = painterResource(Res.drawable.chevron_left),
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable { onBack() }
            )
            Text(
                text = "ภส. 05-01",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            // พื้นหลัง
            Image(
                painter = painterResource(Res.drawable.bg_main),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // รายการเมนูย่อย
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(submenuItems) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable { onItemClick(item.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            // แสดงไอคอนจากไฟล์ภาพ PNG ด้วย Image
                            Image(
                                painter = painterResource(item.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                painter = painterResource(Res.drawable.chevron_righ),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}