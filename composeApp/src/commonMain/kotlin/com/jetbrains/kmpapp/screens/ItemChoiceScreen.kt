package com.jetbrains.kmpapp.screens.choice

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main
import ismartaudit.composeapp.generated.resources.chevron_left
import ismartaudit.composeapp.generated.resources.chevron_righ
import ismartaudit.composeapp.generated.resources.icon_document
import ismartaudit.composeapp.generated.resources.notify

/**
 * Model สำหรับตัวเลือกรายการ
 */
data class ItemChoiceType(
    val id: String,
    val title: String,
    val iconRes: DrawableResource,
    val enabled: Boolean = true
)

/**
 * หน้าเลือกประเภทรายการ (รูปภาพ/PDF/ใบขน)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemChoiceScreen(
    sourceType: String, // "approval", "before_export", "export_oil", "export_alcohol", "history"
    onBack: () -> Unit,
    onItemClick: (ItemChoiceType) -> Unit
) {
    // กำหนดรายการตาม sourceType
    val items = when (sourceType) {
        "approval" -> listOf(
            ItemChoiceType("images", "รูปภาพ", Res.drawable.icon_document),
            ItemChoiceType("pdf", "รายละเอียดคำขอ", Res.drawable.notify),
            ItemChoiceType("transport", "ใบขนย้ายสุรา", Res.drawable.icon_document, enabled = false)
        )
        "before_export" -> listOf(
            ItemChoiceType("images", "รูปภาพ", Res.drawable.icon_document),
            ItemChoiceType("pdf", "รายละเอียดคำขอ", Res.drawable.notify),
            ItemChoiceType("transport", "ใบขนย้ายสุรา", Res.drawable.icon_document, enabled = false)
        )
        "export_oil" -> listOf(
            ItemChoiceType("images", "รูปภาพ", Res.drawable.icon_document),
            ItemChoiceType("pdf", "รายละเอียดคำขอ", Res.drawable.notify),
            ItemChoiceType("transport", "ใบขนย้ายสุรา", Res.drawable.icon_document, enabled = false)
        )
        "export_alcohol" -> listOf(
            ItemChoiceType("images", "รูปภาพ", Res.drawable.icon_document),
            ItemChoiceType("pdf", "รายละเอียดคำขอ", Res.drawable.notify),
            ItemChoiceType("transport", "ใบขนย้ายสุรา", Res.drawable.icon_document, enabled = false)
        )
        "history" -> listOf(
            ItemChoiceType("images", "รูปภาพ", Res.drawable.icon_document),
            ItemChoiceType("pdf", "รายละเอียดคำขอ", Res.drawable.notify),
            ItemChoiceType("transport", "ใบขนย้ายสุรา", Res.drawable.icon_document, enabled = false)
        )
        else -> emptyList()
    }

    // กำหนด title ตาม sourceType
    val title = when (sourceType) {
        "approval" -> "การพิจารณาอนุมัติยกเว้นภาษี"
        "before_export" -> "ก่อนนำออกจากโรงงาน (ต้นทาง)"
        "export_oil" -> "ก่อนส่งออกนอกราชอาณาจักร (สำหรับสินค้าน้ำมัน)"
        "export_alcohol" -> "ก่อนส่งออกนอกราชอาณาจักร (สำหรับสินค้าสุราและยาสูบ)"
        "history" -> "ประวัติรายการย้อนหลัง 30 วัน"
        else -> "เลือกรายการ"
    }

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
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.chevron_left),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "เลือกรายการ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "ภส. 05-01",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Background + Content
        Box(modifier = Modifier.weight(1f)) {
            // Background
            Image(
                painter = painterResource(Res.drawable.bg_main),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // Content
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { item ->
                    ItemChoiceCard(
                        item = item,
                        onClick = { if (item.enabled) onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemChoiceCard(
    item: ItemChoiceType,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.enabled) Color.White else Color.Gray.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.enabled) 4.dp else 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(enabled = item.enabled) { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                alpha = if (item.enabled) 1f else 0.5f
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (item.enabled) Color.Black else Color.Gray,
                modifier = Modifier.weight(1f)
            )

            if (item.enabled) {
                Icon(
                    painter = painterResource(Res.drawable.chevron_righ),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}