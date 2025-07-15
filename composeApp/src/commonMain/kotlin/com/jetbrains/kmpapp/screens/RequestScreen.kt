// File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/screens/request/RequestScreen.kt
package com.jetbrains.kmpapp.screens.request

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main
import ismartaudit.composeapp.generated.resources.chevron_left
import ismartaudit.composeapp.generated.resources.chevron_righ
import ismartaudit.composeapp.generated.resources.icon_document
import ismartaudit.composeapp.generated.resources.qr_code

/**
 * UI แบบมี TopBar + Background ตามตัวอย่าง
 * Logic สำหรับสร้างและกรองเมนูจาก formCode, signatureStatus, dutyCodeFlag, historyFlag
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    formCode: String,
    signatureStatus: Int,
    dutyCodeFlag: Int?,
    historyFlag: Boolean,
    onBack: () -> Unit,
    onScan: () -> Unit,
    onItemClick: (RequestMenuItem) -> Unit
) {
    // กรณีพิเศษ PS28A / KEEP28A
    val isSpecial = formCode == "PS28A" || formCode == "KEEP28A"

    // สร้างรายการเมนูพร้อมค่าฟิกสำหรับส่งต่อ
    val allItems = listOf(
        RequestMenuItem(
            title = "การพิจารณาอนุมัติยกเว้นภาษี",
            route = "/staff/exap_05010/main/menu01/sub",
            signatureStatus = signatureStatus,
            dutyCodeFlag = if (isSpecial) 3 else null
        ),
        RequestMenuItem(
            title = "ก่อนนำออกจากโรงงาน (ต้นทาง)",
            route = "/staff/exap_05010/main/menu01/item_list",
            signatureStatus = 3
        ),
        RequestMenuItem(
            title = "ก่อนส่งออกนอกราชอาณาจักร (สำหรับสินค้าน้ำมัน)",
            route = "/staff/exap_05010/main/menu01/item_list",
            signatureStatus = 4,
            dutyCodeFlag = 1
        ),
        RequestMenuItem(
            title = "ก่อนส่งออกนอกราชอาณาจักร (สำหรับสินค้าสุราและยาสูบ)",
            route = "/staff/exap_05010/main/menu01/item_list",
            signatureStatus = 4,
            dutyCodeFlag = 2
        ),
        RequestMenuItem(
            title = "ประวัติการยื่นคำขอย้อนหลัง 30 วัน",
            route = "/staff/exap_05010/main/menu01/item_list",
            signatureStatus = signatureStatus,
            historyFlag = true,
            dutyCodeFlag = if (isSpecial) 3 else null
        )
    )

    // กรองรายการตาม formCode (special แสดงเฉพาะ dutyCodeFlag=3)
    val visibleItems = remember(formCode) {
        if (isSpecial) allItems.filter { it.dutyCodeFlag == 3 }
        else allItems
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
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    painter = painterResource(Res.drawable.chevron_left),
                    contentDescription = "ย้อนกลับ",
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "รายการคำขอ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "ภส. 05-01",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.DarkGray
                )
            }
            if (formCode == "PS28") {
                IconButton(
                    onClick = onScan,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.qr_code),
                        contentDescription = "สแกน",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Background + List
        Box(modifier = Modifier.weight(1f)) {
            Image(
                    painter = painterResource(Res.drawable.bg_main),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(visibleItems) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clickable { onItemClick(item) }
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.icon_document),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = item.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Image(
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

/**
 * Model สำหรับเมนู
 */
data class RequestMenuItem(
    val title: String,
    val route: String,
    val signatureStatus: Int,
    val dutyCodeFlag: Int? = null,
    val historyFlag: Boolean = false
)
