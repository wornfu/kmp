// File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/screens/profile/ProfileScreen.kt
package com.jetbrains.kmpapp.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.chevron_left
import ismartaudit.composeapp.generated.resources.myprofile
import ismartaudit.composeapp.generated.resources.person

@Composable
fun ProfileScreen(
    username: String,
    fullName: String,
    position: String,
    unit: String,
    version: String,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .navigationBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {
        // TopAppBar
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .background(Color(0xFFF9F9F9))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ปุ่ม Back ซ้าย
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    painter = painterResource(Res.drawable.chevron_left),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
            // ชื่อหน้า
            Text(
                text = "บัญชีผู้ใช้",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )
            // ปุ่ม Home (เรียก onBack เช่นกัน)
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    painter = painterResource(Res.drawable.person),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFF42A5F5))
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(Res.drawable.myprofile),
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(16.dp))

        // ชื่อ-username
        Text(
            text = "$fullName ($username)",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(8.dp))

        // ตำแหน่ง
        Text(
            text = position,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(4.dp))

        // หน่วยงาน
        Text(
            text = unit,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.weight(1f))

        // Version
        Text(
            text = version,
            fontSize = 12.sp,
            color = Color(0xFF42A5F5),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )

        // Logout button
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("ออกจากระบบ", color = Color.White)
        }

        Spacer(Modifier.height(16.dp))
    }
}
