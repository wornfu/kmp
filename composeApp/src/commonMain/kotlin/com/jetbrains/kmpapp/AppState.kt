// File: commonMain/kotlin/com/jetbrains/kmpapp/AppState.kt
package com.jetbrains.kmpapp

import androidx.compose.runtime.*
import com.jetbrains.kmpapp.screens.notification.NotificationItem
import com.jetbrains.kmpapp.screens.filter.FilterData

// App State Class
@Stable
class AppState {
    var currentScreen by mutableStateOf<Screen>(Screen.Login)
        private set

    var userSession by mutableStateOf<UserSession?>(null)
        private set

    var signatureStatus by mutableStateOf(4)
        private set

    var dutyCodeFlag by mutableStateOf<Int?>(1)
        private set

    var historyFlag by mutableStateOf(true)
        private set

    var notifications by mutableStateOf(demoNotifications)
        private set

    // Stack สำหรับเก็บประวัติการนำทาง
    private val navigationStack = mutableListOf<Screen>()

    // Navigation functions
    fun navigateTo(screen: Screen) {
        // เก็บหน้าปัจจุบันใน stack ก่อนไปหน้าใหม่
        navigationStack.add(currentScreen)
        currentScreen = screen
    }

    fun navigateToWithReplace(screen: Screen) {
        // ไปหน้าใหม่โดยไม่เก็บใน stack (แทนที่หน้าปัจจุบัน)
        currentScreen = screen
    }

    fun navigateBack() {
        // ถ้ามี stack ให้กลับไปหน้าก่อนหน้า
        if (navigationStack.isNotEmpty()) {
            currentScreen = navigationStack.removeLastOrNull() ?: Screen.Menu
        } else {
            // ถ้าไม่มี stack แสดงว่าอยู่หน้าแรกแล้ว
            currentScreen = when (currentScreen) {
                is Screen.Menu -> Screen.Login
                else -> Screen.Menu
            }
        }
    }

    // User session management
    fun login(username: String, userInfo: UserInfo) {
        userSession = UserSession(username, userInfo)
        // เคลียร์ stack เมื่อ login
        navigationStack.clear()
        currentScreen = Screen.Menu
    }

    fun logout() {
        userSession = null
        signatureStatus = 0
        dutyCodeFlag = null
        historyFlag = false
        // เคลียร์ stack เมื่อ logout
        navigationStack.clear()
        currentScreen = Screen.Login
    }

    // Notification management
    fun markNotificationAsRead(index: Int) {
        val updatedNotifications = notifications.toMutableList()
        if (index in 0 until updatedNotifications.size) {
            updatedNotifications[index] = updatedNotifications[index].copy(isNew = false)
            notifications = updatedNotifications
        }
    }

    // ⭐ เพิ่มฟังก์ชันลบแจ้งเตือน
    fun removeNotification(index: Int) {
        val updatedNotifications = notifications.toMutableList()
        if (index in 0 until updatedNotifications.size) {
            updatedNotifications.removeAt(index)
            notifications = updatedNotifications
        }
    }

    // ⭐ เพิ่มฟังก์ชันลบแจ้งเตือนทั้งหมด (optional)
    fun clearAllNotifications() {
        notifications = emptyList()
    }

    val notificationCount: Int
        get() = notifications.count { it.isNew }
}

// User session data
data class UserSession(
    val username: String,
    val userInfo: UserInfo
)

data class UserInfo(
    val fullName: String,
    val position: String,
    val unit: String
)

// Composable for providing app state
@Composable
fun rememberAppState(): AppState {
    return remember { AppState() }
}

// Demo data - เพิ่มข้อมูลทดสอบเพิ่มเติม
private val demoNotifications = listOf(
    NotificationItem(
        title = "สรุปรายงาน ภส.05-01",
        subtitle = "มีแบบ ภส.05-01 รออนุมัติ 3 รายการ",
        timestamp = "1 ก.ค. 2568 10:15",
        isNew = true
    ),
    NotificationItem(
        title = "ระบบอัปเดตเวอร์ชัน",
        subtitle = "Version 1.2.8 พร้อมใช้งานแล้ว",
        timestamp = "30 มิ.ย. 2568 16:40",
        isNew = false
    ),
    NotificationItem(
        title = "การอนุมัติคำขอ",
        subtitle = "คำขอของท่านได้รับการอนุมัติแล้ว",
        timestamp = "29 มิ.ย. 2568 14:30",
        isNew = true
    ),
    NotificationItem(
        title = "แจ้งเตือนบำรุงรักษาระบบ",
        subtitle = "ระบบจะหยุดให้บริการชั่วคราวในวันที่ 5 ก.ค.",
        timestamp = "28 มิ.ย. 2568 09:00",
        isNew = false
    )
)