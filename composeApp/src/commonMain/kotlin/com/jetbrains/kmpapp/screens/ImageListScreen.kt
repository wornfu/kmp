// File: shared/src/commonMain/kotlin/com/jetbrains/kmpapp/screens/images/ImageListScreen.kt
package com.jetbrains.kmpapp.screens.images

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import ismartaudit.composeapp.generated.resources.Res
import ismartaudit.composeapp.generated.resources.bg_main
import ismartaudit.composeapp.generated.resources.chevron_left
// ⭐ เพิ่มการ import สำหรับ AsyncImage
import coil3.compose.AsyncImage

// Interface สำหรับ com.jetbrains.kmpapp.ImagePicker - ย้ายมาไว้ด้านบน
interface ImagePicker {
    fun pickFromGallery(onResult: (String?) -> Unit)
    fun takePhoto(onResult: (String?) -> Unit)
}

// Data class สำหรับรูปภาพ
data class ImageItem(
    val id: String,
    val uri: String,
    val name: String = "",
    val isSelected: Boolean = false
)

// Data class สำหรับตัวเลือกการเพิ่มรูป
data class AddImageOption(
    val id: String,
    val title: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onDeleteAll: () -> Unit,
    onSave: () -> Unit,
    imagePicker: ImagePicker? = null
) {
    // State management
    var images by remember { mutableStateOf<List<ImageItem>>(emptyList()) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    // Function เพื่อเพิ่มรูปใหม่
    fun addNewImage(uri: String) {
        val newImage = ImageItem(
            id = "img_${kotlin.random.Random.nextLong()}",
            uri = uri,
            name = "ภาพที่ ${images.size + 1}"
        )
        images = images + newImage
    }

    // เริ่มต้นด้วย empty state
    LaunchedEffect(Unit) {
        images = emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar
        TopBar(
            isSelectionMode = isSelectionMode,
            selectedCount = images.count { it.isSelected },
            onBack = onBack,
            onCancelSelection = {
                isSelectionMode = false
                images = images.map { it.copy(isSelected = false) }
            },
            onEnterSelectionMode = {
                isSelectionMode = true
            }
        )

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

            if (images.isEmpty()) {
                // Empty State
                EmptyStateContent()
            } else {
                // Image Grid
                ImageGrid(
                    images = images,
                    isSelectionMode = isSelectionMode,
                    onImageClick = { index ->
                        if (isSelectionMode) {
                            // Toggle selection
                            images = images.mapIndexed { i, image ->
                                if (i == index) image.copy(isSelected = !image.isSelected)
                                else image
                            }
                        } else {
                            // View image (TODO: implement image viewer)
                        }
                    },
                    onImageLongPress = { index ->
                        if (!isSelectionMode) {
                            isSelectionMode = true
                            images = images.mapIndexed { i, image ->
                                if (i == index) image.copy(isSelected = true)
                                else image
                            }
                        }
                    }
                )
            }

            // ปุ่ม + สำหรับเพิ่มรูป (แสดงเมื่อไม่อยู่ในโหมดเลือก)
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = Color.White,
                    contentColor = Color(0xFF2196F3),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "เพิ่มรูปภาพ",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Bottom Controls - แสดงเสมอ ไม่ว่าจะมีรูปหรือไม่
        BottomControls(
            isVisible = true,
            hasSelectedImages = images.any { it.isSelected },
            onDeleteAll = {
                val selectedImages = images.filter { it.isSelected }
                if (selectedImages.isNotEmpty()) {
                    // ลบรูปที่เลือก
                    images = images.filter { !it.isSelected }
                    if (images.isEmpty()) {
                        isSelectionMode = false
                    }
                }
            },
            onSave = onSave
        )
    }

    // Bottom Sheet สำหรับเลือกวิธีเพิ่มรูป
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = Color.White
        ) {
            AddImageBottomSheet(
                onOptionSelected = { option ->
                    showBottomSheet = false
                    when (option.id) {
                        "gallery" -> {
                            // เปิดคลังรูปภาพ
                            if (imagePicker != null) {
                                imagePicker.pickFromGallery { uri ->
                                    if (uri != null) {
                                        addNewImage(uri)
                                    }
                                }
                            } else {
                                // Fallback สำหรับทดสอบ - เพิ่มรูป mock
                                addNewImage("mock_gallery_${kotlin.random.Random.nextLong()}")
                            }
                        }
                        "camera" -> {
                            // เปิดกล้อง
                            if (imagePicker != null) {
                                imagePicker.takePhoto { uri ->
                                    if (uri != null) {
                                        addNewImage(uri)
                                    }
                                }
                            } else {
                                // Fallback สำหรับทดสอบ - เพิ่มรูป mock
                                addNewImage("mock_camera_${kotlin.random.Random.nextLong()}")
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun TopBar(
    isSelectionMode: Boolean,
    selectedCount: Int,
    onBack: () -> Unit,
    onCancelSelection: () -> Unit,
    onEnterSelectionMode: () -> Unit
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // ปุ่ม back
        IconButton(
            onClick = if (isSelectionMode) onCancelSelection else onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(Res.drawable.chevron_left),
                contentDescription = if (isSelectionMode) "ยกเลิกการเลือก" else "ย้อนกลับ",
                modifier = Modifier.size(24.dp)
            )
        }

        // ชื่อหน้า หรือ จำนวนที่เลือก ตรงกลาง
        Text(
            text = if (isSelectionMode) "เลือกแล้ว $selectedCount รูป" else "รูปภาพปลายทาง",
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )

        // ปุ่มแก้ไข/ยกเลิก ด้านขวาบน (แสดงเสมอ)
        TextButton(
            onClick = {
                if (isSelectionMode) {
                    // ยกเลิกการเลือก
                    onCancelSelection()
                } else {
                    // เข้าโหมดแก้ไข/เลือก
                    onEnterSelectionMode()
                }
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(
                text = if (isSelectionMode) "ยกเลิก" else "เลือก",
                color = Color(0xFF2196F3),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun EmptyStateContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ไอคอนแกลเลอรี่
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Gray.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.Gray
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "ยังไม่มีข้อมูลระบบ",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ImageGrid(
    images: List<ImageItem>,
    isSelectionMode: Boolean,
    onImageClick: (Int) -> Unit,
    onImageLongPress: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // ⭐ ใช้ GridCells แทน StaggeredGridCells
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp), // ⭐ เปลี่ยนจาก verticalItemSpacing
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(images) { index, image ->
            ImageItemCard(
                image = image,
                isSelectionMode = isSelectionMode,
                onClick = { onImageClick(index) },
                onLongPress = { onImageLongPress(index) }
            )
        }
    }
}

@Composable
private fun ImageItemCard(
    image: ImageItem,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongPress() }
                )
            }
            .then(
                if (image.isSelected) {
                    Modifier.border(3.dp, Color.Red, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
    ) {
        // ⭐ ใช้ AsyncImage สำหรับแสดงรูปจริง
        if (image.uri.startsWith("content://") || image.uri.startsWith("file://")) {
            // รูปจริงจากเครื่อง
            AsyncImage(
                model = image.uri,
                contentDescription = image.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            // Mock image (ใช้สีแทนรูปจริงสำหรับทดสอบ)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        when (image.id.takeLast(1)) {
                            "1" -> Color(0xFF4CAF50)
                            "2" -> Color(0xFF2196F3)
                            "3" -> Color(0xFFFF9800)
                            "4" -> Color(0xFF9C27B0)
                            else -> Color.Gray
                        },
                        RoundedCornerShape(8.dp)
                    )
            )
        }

        // Selection indicator
        if (isSelectionMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .background(
                        if (image.isSelected) Color.Red else Color.White.copy(alpha = 0.8f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (image.isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "เลือกแล้ว",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomControls(
    isVisible: Boolean,
    hasSelectedImages: Boolean,
    onDeleteAll: () -> Unit,
    onSave: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ปุ่มลบ - ⭐ ปรับให้ไม่สามารถกดได้เมื่อไม่ได้เลือกรูป
            Button(
                onClick = onDeleteAll,
                enabled = hasSelectedImages, // ⭐ เปิดใช้งานเมื่อมีรูปที่เลือกเท่านั้น
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373),
                    disabledContainerColor = Color(0xFFE57373).copy(alpha = 0.5f) // ⭐ สีเมื่อ disabled
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ลบทั้งหมด", // ⭐ ใช้ข้อความเดียวเสมอ
                    fontSize = 16.sp
                )
            }

            // ปุ่มบันทึก
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "บันทึก",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun AddImageBottomSheet(
    onOptionSelected: (AddImageOption) -> Unit
) {
    val options = listOf(
        AddImageOption(
            id = "gallery",
            title = "เลือกรูปภาพจากในเครื่อง",
            icon = {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        ),
        AddImageOption(
            id = "camera",
            title = "เปิดกล้องถ่ายรูป",
            icon = {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // หัวข้อ Bottom Sheet
        Text(
            text = "เพิ่มรูปภาพ",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        options.forEach { option ->
            Card(
                onClick = { onOptionSelected(option) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    option.icon()
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}