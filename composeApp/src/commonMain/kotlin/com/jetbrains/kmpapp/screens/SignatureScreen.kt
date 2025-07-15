// File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/screens/signature/SignatureScreen.kt
package com.jetbrains.kmpapp.screens.signature

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main

// Data class for signature entries
data class SignatureOption(
    val id: String,
    val name: String,
    val username: String,
    val position: String,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureScreen(
    topicId: String,
    sourceType: String = "single",
    onBack: () -> Unit,
    onSubmit: (String) -> Unit
) {
    // State
    var selectedOption by remember { mutableStateOf("เห็นควรยกเว้นภาษี") }
    var reasonText by remember { mutableStateOf("") }
    var hasSignature by remember { mutableStateOf(false) }
    var selectedSignature by remember { mutableStateOf<SignatureOption?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Mock options
    val signatureOptions = listOf(
        SignatureOption("rtn07", "Rtn 07", "test_rtn07", "นักวิชาการสรรพสามิตชำนาญการ", "30 พ.ค. 2568"),
        SignatureOption("rtn03", "Rtn 03", "test_rtn03", "นักวิชาการสรรพสามิตชำนาญการ", "05 ก.ค. 2568"),
        SignatureOption("rtn01", "Rtn 01", "test_rtn01", "นักวิชาการสรรพสามิตชำนาญการ", "04 ก.ค. 2568"),
        SignatureOption("rtn04", "Rtn 04", "test_rtn04", "นักวิชาการสรรพสามิตชำนาญการ", "06 ก.ค. 2568")
    )

    val options = listOf("เห็นควรยกเว้นภาษี", "ไม่เห็นควรยกเว้นภาษี")
    val screenTitle = when (sourceType) {
        "inspection" -> "ความเห็นของเจ้าพนักงาน (หลังตรวจสอบ)"
        "multiple" -> "ความเห็นของเจ้าพนักงาน (หลายเอกสาร)"
        else -> "ความเห็นของเจ้าพนักงาน"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = screenTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding() // ย้ายมาไว้ที่ Column
            ) {
                Button(
                    onClick = { onSubmit(topicId) },
                    enabled = hasSignature && reasonText.isNotBlank(),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2979FF),
                        disabledContainerColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "ลงลายมือชื่อเห็นของเจ้าพนักงาน",
                        fontSize = 16.sp,
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                item {
                    // Radio Options - ลบ Card ออก
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        options.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp) // เพิ่มความสูงให้เท่ากับช่องเลือกผู้อนุมัติ
                                    .selectable(
                                        selected = selectedOption == option,
                                        onClick = { selectedOption = option }
                                    )
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedOption == option,
                                    onClick = { selectedOption = option },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2979FF))
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(option, fontSize = 16.sp, color = Color.Black)
                            }
                        }
                    }
                }

                item {
                    // Dropdown Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "กรุณาเลือกผู้อนุมัติ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text("*", fontSize = 16.sp, color = Color.Red)
                            }
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .background(
                                        if (reasonText.isNotBlank()) Color.White else Color(0xFFF5F5F5),
                                        RoundedCornerShape(8.dp)
                                    ) // เปลี่ยนเป็นพื้นหลังขาวเมื่อเลือกแล้ว
                                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp)
                                    .clickable { showBottomSheet = true },
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        if (reasonText.isBlank()) "" else reasonText,
                                        fontSize = 14.sp,
                                        color = if (reasonText.isBlank()) Color.Gray else Color.Black
                                    )
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    // Signature Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "ลายเซ็น",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(
                                        if (selectedSignature != null) Color.White else Color.Gray.copy(alpha = 0.1f),
                                        RoundedCornerShape(8.dp)
                                    ) // เปลี่ยนพื้นหลังเป็นสีขาวเมื่อมีการเลือกแล้ว
                                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showBottomSheet = true },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedSignature != null) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        // Draw signature
                                        Canvas(modifier = Modifier.size(140.dp, 80.dp)) {
                                            val w = size.width
                                            val h = size.height
                                            val strokeWidth = 4f

                                            when (selectedSignature!!.id) {
                                                "rtn07" -> {
                                                    // Draw "Rtn 07"
                                                    val path = Path().apply {
                                                        // R
                                                        moveTo(w * 0.1f, h * 0.3f)
                                                        lineTo(w * 0.1f, h * 0.7f)
                                                        moveTo(w * 0.1f, h * 0.3f)
                                                        lineTo(w * 0.2f, h * 0.3f)
                                                        lineTo(w * 0.25f, h * 0.45f)
                                                        lineTo(w * 0.2f, h * 0.5f)
                                                        lineTo(w * 0.1f, h * 0.5f)
                                                        moveTo(w * 0.2f, h * 0.5f)
                                                        lineTo(w * 0.25f, h * 0.7f)
                                                        // t
                                                        moveTo(w * 0.35f, h * 0.4f)
                                                        lineTo(w * 0.35f, h * 0.7f)
                                                        moveTo(w * 0.3f, h * 0.45f)
                                                        lineTo(w * 0.4f, h * 0.45f)
                                                        // n
                                                        moveTo(w * 0.5f, h * 0.45f)
                                                        lineTo(w * 0.5f, h * 0.7f)
                                                        moveTo(w * 0.5f, h * 0.5f)
                                                        lineTo(w * 0.55f, h * 0.45f)
                                                        lineTo(w * 0.6f, h * 0.5f)
                                                        lineTo(w * 0.6f, h * 0.7f)
                                                        // 07
                                                        moveTo(w * 0.7f, h * 0.3f)
                                                        addOval(androidx.compose.ui.geometry.Rect(
                                                            w * 0.7f, h * 0.3f, w * 0.8f, h * 0.6f
                                                        ))
                                                        moveTo(w * 0.85f, h * 0.3f)
                                                        lineTo(w * 0.95f, h * 0.3f)
                                                        lineTo(w * 0.9f, h * 0.6f)
                                                    }
                                                    drawPath(
                                                        path = path,
                                                        color = androidx.compose.ui.graphics.Color.Black,
                                                        style = Stroke(width = strokeWidth)
                                                    )
                                                }
                                                "rtn03" -> {
                                                    // Draw "Rtn 03"
                                                    val path = Path().apply {
                                                        // R
                                                        moveTo(w * 0.1f, h * 0.3f)
                                                        lineTo(w * 0.1f, h * 0.7f)
                                                        moveTo(w * 0.1f, h * 0.3f)
                                                        lineTo(w * 0.2f, h * 0.3f)
                                                        lineTo(w * 0.25f, h * 0.45f)
                                                        lineTo(w * 0.2f, h * 0.5f)
                                                        lineTo(w * 0.1f, h * 0.5f)
                                                        moveTo(w * 0.2f, h * 0.5f)
                                                        lineTo(w * 0.25f, h * 0.7f)
                                                        // t
                                                        moveTo(w * 0.35f, h * 0.4f)
                                                        lineTo(w * 0.35f, h * 0.7f)
                                                        moveTo(w * 0.3f, h * 0.45f)
                                                        lineTo(w * 0.4f, h * 0.45f)
                                                        // n
                                                        moveTo(w * 0.5f, h * 0.45f)
                                                        lineTo(w * 0.5f, h * 0.7f)
                                                        moveTo(w * 0.5f, h * 0.5f)
                                                        lineTo(w * 0.55f, h * 0.45f)
                                                        lineTo(w * 0.6f, h * 0.5f)
                                                        lineTo(w * 0.6f, h * 0.7f)
                                                        // 03
                                                        moveTo(w * 0.7f, h * 0.3f)
                                                        addOval(androidx.compose.ui.geometry.Rect(
                                                            w * 0.7f, h * 0.3f, w * 0.8f, h * 0.6f
                                                        ))
                                                        moveTo(w * 0.85f, h * 0.3f)
                                                        lineTo(w * 0.95f, h * 0.3f)
                                                        lineTo(w * 0.9f, h * 0.45f)
                                                        lineTo(w * 0.95f, h * 0.45f)
                                                        lineTo(w * 0.9f, h * 0.6f)
                                                        lineTo(w * 0.85f, h * 0.6f)
                                                    }
                                                    drawPath(
                                                        path = path,
                                                        color = androidx.compose.ui.graphics.Color.Black,
                                                        style = Stroke(width = strokeWidth)
                                                    )
                                                }
                                                "rtn01" -> {
                                                    // Draw "Rtn 01"
                                                    val path = Path().apply {
                                                        // R
                                                        moveTo(w * 0.1f, h * 0.3f)
                                                        lineTo(w * 0.1f, h * 0.7f)
                                                        moveTo(w * 0.1f, h * 0.3f)
                                                        lineTo(w * 0.2f, h * 0.3f)
                                                        lineTo(w * 0.25f, h * 0.45f)
                                                        lineTo(w * 0.2f, h * 0.5f)
                                                        lineTo(w * 0.1f, h * 0.5f)
                                                        moveTo(w * 0.2f, h * 0.5f)
                                                        lineTo(w * 0.25f, h * 0.7f)
                                                        // t
                                                        moveTo(w * 0.35f, h * 0.4f)
                                                        lineTo(w * 0.35f, h * 0.7f)
                                                        moveTo(w * 0.3f, h * 0.45f)
                                                        lineTo(w * 0.4f, h * 0.45f)
                                                        // n
                                                        moveTo(w * 0.5f, h * 0.45f)
                                                        lineTo(w * 0.5f, h * 0.7f)
                                                        moveTo(w * 0.5f, h * 0.5f)
                                                        lineTo(w * 0.55f, h * 0.45f)
                                                        lineTo(w * 0.6f, h * 0.5f)
                                                        lineTo(w * 0.6f, h * 0.7f)
                                                        // 01
                                                        moveTo(w * 0.7f, h * 0.3f)
                                                        addOval(androidx.compose.ui.geometry.Rect(
                                                            w * 0.7f, h * 0.3f, w * 0.8f, h * 0.6f
                                                        ))
                                                        moveTo(w * 0.9f, h * 0.3f)
                                                        lineTo(w * 0.9f, h * 0.6f)
                                                        moveTo(w * 0.85f, h * 0.35f)
                                                        lineTo(w * 0.9f, h * 0.3f)
                                                    }
                                                    drawPath(
                                                        path = path,
                                                        color = androidx.compose.ui.graphics.Color.Black,
                                                        style = Stroke(width = strokeWidth)
                                                    )
                                                }
                                                else -> {
                                                    // Default signature
                                                    val path = Path().apply {
                                                        moveTo(w * 0.2f, h * 0.5f)
                                                        lineTo(w * 0.8f, h * 0.5f)
                                                    }
                                                    drawPath(
                                                        path = path,
                                                        color = androidx.compose.ui.graphics.Color.Black,
                                                        style = Stroke(width = strokeWidth)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        Text(
                                            "นายลงความเห็น ${selectedSignature!!.username}",
                                            fontSize = 14.sp,
                                            color = Color(0xFF2979FF),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            selectedSignature!!.position,
                                            fontSize = 12.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            selectedSignature!!.date,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            "กดเพื่อเลือกลายเซ็น",
                                            fontSize = 16.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "เลือกลายเซ็นจากรายการ",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            if (selectedSignature != null) {
                                // ลบปุ่มเคลียร์ออก
                            }
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheet for signature selection
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // แสดงแค่ชื่อและเส้นกั้น ไม่ใช้ Card
                signatureOptions.forEach { option ->
                    Text(
                        text = "นายตรวจสินค้า ${option.username}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedSignature = option
                                reasonText = " ${option.name}"
                                hasSignature = true
                                showBottomSheet = false
                            }
                            .padding(vertical = 16.dp)
                    )

                    if (option != signatureOptions.last()) {
                        HorizontalDivider(
                            color = Color.Gray.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}