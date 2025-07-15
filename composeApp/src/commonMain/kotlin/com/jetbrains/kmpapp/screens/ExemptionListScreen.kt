package com.jetbrains.kmpapp.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main
import ismartaudit.composeapp.generated.resources.chevron_left
import ismartaudit.composeapp.generated.resources.chevron_righ
import ismartaudit.composeapp.generated.resources.icon_document

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExemptionListScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        Box(
            Modifier
                .statusBarsPadding()
                .background(Color.White)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Image(
                    painter = painterResource(Res.drawable.chevron_left),
                    contentDescription = "ย้อนกลับ",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "แบบรายการขอยกเว้น",
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            Image(
                painter = painterResource(Res.drawable.bg_main),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            val items = listOf(
                "เจ้าพนักงานผู้ตรวจสอบ",
                "เจ้าพนักงานผู้มีความเห็น",
                "อนุมัติ",
                "ประวัติรายการย้อนหลัง 30 วัน"
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { title ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = cardColors(containerColor = Color.White),
                        elevation = cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable { /* TODO: onClick title */ }
                    ) {
                        Row(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.icon_document),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(text = title, fontSize = 16.sp, modifier = Modifier.weight(1f))
                            Image(
                                painter = painterResource(Res.drawable.chevron_righ),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}
