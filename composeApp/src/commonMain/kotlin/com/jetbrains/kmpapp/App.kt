// File: commonMain/kotlin/com/jetbrains/kmpapp/App.kt
package com.jetbrains.kmpapp

import com.jetbrains.kmpapp.screens.notification.NotificationItem
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.jetbrains.kmpapp.Screen.ItemChoice
import com.jetbrains.kmpapp.Screen.PdfList
import com.jetbrains.kmpapp.screens.login.LoginScreen
import com.jetbrains.kmpapp.screens.menu.MenuScreen
import com.jetbrains.kmpapp.screens.notification.NotificationScreen
import com.jetbrains.kmpapp.screens.profile.ProfileScreen
import com.jetbrains.kmpapp.screens.request.RequestScreen
import com.jetbrains.kmpapp.screens.qrScanner.QrScannerView
import com.jetbrains.kmpapp.screens.ExemptionListScreen
import com.jetbrains.kmpapp.screens.submenu.SubMenuScreen
import com.jetbrains.kmpapp.screens.pdf.PdfListScreen
import com.jetbrains.kmpapp.screens.pdf.PdfDetailScreen
import com.jetbrains.kmpapp.screens.pdf.PdfItem
import com.jetbrains.kmpapp.screens.choice.ItemChoiceScreen
import com.jetbrains.kmpapp.screens.choice.ItemChoiceType
import com.jetbrains.kmpapp.screens.images.ImageListScreen
import com.jetbrains.kmpapp.screens.images.ImagePicker
import com.jetbrains.kmpapp.screens.history.HistoryListScreen
import com.jetbrains.kmpapp.screens.history.HistoryItem
import com.jetbrains.kmpapp.screens.filter.DateFilterScreen
import com.jetbrains.kmpapp.screens.filter.FilterData
import com.jetbrains.kmpapp.screens.signature.SignatureScreen
import com.jetbrains.kmpapp.screens.inspection.InspectionScreen
import com.jetbrains.kmpapp.screens.document.DocumentSelectionScreen
import com.jetbrains.kmpapp.screens.document.DocumentItem
import com.jetbrains.kmpapp.screens.transport.TransportDocumentScreen

sealed class Screen {
    object Login        : Screen()
    object Menu         : Screen()
    object Notification : Screen()
    object Profile      : Screen()
    object ExemptionList: Screen()
    object QrScanner    : Screen()

    data class Request(
        val fromCode: String,
        val signatureStatus: Int,
        val dutyCodeFlag: Int?,
        val historyFlag: Boolean
    ) : Screen()

    data class SubMenu(
        val parentRequest: Request
    ) : Screen()

    data class ItemChoice(
        val sourceType: String, // "approval", "before_export", "export_oil", "export_alcohol", "history"
        val parentRequest: Request? = null,
        val parentSubMenu: SubMenu? = null
    ) : Screen()

    data class PdfList(
        val sourceType: String,
        val parentChoice: ItemChoice? = null,
        val parentSubMenu: SubMenu? = null
    ) : Screen()

    data class PdfDetail(
        val parentPdfList: PdfList,
        val selectedPdf: PdfItem
    ) : Screen()

    data class Inspection(
        val sourceType: String,
        val parentPdfDetail: PdfDetail,
        val topicId: String
    ) : Screen()

    data class Signature(
        val sourceType: String, // "single", "multiple", "inspection"
        val parentPdfDetail: PdfDetail? = null,
        val parentInspection: Inspection? = null,
        val topicId: String
    ) : Screen()

    data class ImageList(
        val sourceType: String,
        val parentChoice: ItemChoice,
        val topicId: String
    ) : Screen()

    data class HistoryList(
        val parentRequest: Request,
        val currentFilter: FilterData = FilterData()
    ) : Screen()

    data class DateFilter(
        val parentHistory: HistoryList,
        val currentFilter: FilterData = FilterData()
    ) : Screen()

    data class DocumentSelection(
        val sourceType: String,
        val parentPdfList: PdfList,
        val selectedPdf: PdfItem
    ) : Screen()

    data class TransportDocument(
        val sourceType: String,
        val parentDocumentSelection: DocumentSelection,
        val topicId: String
    ) : Screen()
}

@Composable
fun App(imagePicker: ImagePicker? = null) {
    val appState = rememberAppState()

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface(Modifier.fillMaxSize()) {
            AppNavigation(appState = appState, imagePicker = imagePicker)
        }
    }
}

@Composable
private fun AppNavigation(appState: AppState, imagePicker: ImagePicker? = null) {
    when (val screen = appState.currentScreen) {
        is Screen.Login -> {
            LoginScreen(
                username = "",
                onUsernameChange = {},
                password = "",
                onPasswordChange = {},
                isLoading = false,
                onLoginClick = {
                    appState.login(
                        username = "test_user",
                        userInfo = UserInfo(
                            fullName = "นายสองความถึก test_rtn01",
                            position = "นักวิชาการสรรพสามิตชำนาญการ",
                            unit = "หน่วยงาน : สสป.บางปะกง 1"
                        )
                    )
                },
                errorMessage = null
            )
        }

        is Screen.Menu -> {
            MenuScreen(
                notificationCount = appState.notificationCount,
                onItemClick = { formCode ->
                    val targetScreen = if (formCode == "SSO105") {
                        Screen.ExemptionList
                    } else {
                        Screen.Request(
                            fromCode = formCode,
                            signatureStatus = appState.signatureStatus,
                            dutyCodeFlag = appState.dutyCodeFlag,
                            historyFlag = appState.historyFlag
                        )
                    }
                    appState.navigateTo(targetScreen)
                },
                onProfileClick = { appState.navigateTo(Screen.Profile) },
                onNotificationClick = { appState.navigateTo(Screen.Notification) }
            )
        }

        is Screen.Notification -> {
            NotificationScreen(
                items = appState.notifications,
                onBack = { appState.navigateBack() },
                onSettings = { /* TODO: implement settings */ },
                onItemClick = { item: NotificationItem ->
                    val index = appState.notifications.indexOf(item)
                    if (index != -1) {
                        appState.markNotificationAsRead(index)
                    }
                },
                onDeleteItem = { index: Int ->
                    appState.removeNotification(index)
                }
            )
        }

        is Screen.Profile -> {
            ProfileScreen(
                username = appState.userSession?.username ?: "",
                fullName = appState.userSession?.userInfo?.fullName ?: "",
                position = appState.userSession?.userInfo?.position ?: "",
                unit = appState.userSession?.userInfo?.unit ?: "",
                version = "Version: 1.2.1",
                onLogout = { appState.logout() },
                onBack = { appState.navigateBack() }
            )
        }

        is Screen.Request -> {
            RequestScreen(
                formCode = screen.fromCode,
                signatureStatus = screen.signatureStatus,
                dutyCodeFlag = screen.dutyCodeFlag,
                historyFlag = screen.historyFlag,
                onBack = {
                    appState.navigateToWithReplace(Screen.Menu)
                },
                onScan = { appState.navigateTo(Screen.QrScanner) },
                onItemClick = { item ->
                    when (item.title) {
                        "การพิจารณาอนุมัติยกเว้นภาษี" -> {
                            appState.navigateTo(Screen.SubMenu(screen))
                        }
                        "ก่อนนำออกจากโรงงาน (ต้นทาง)" -> {
                            appState.navigateTo(Screen.PdfList("before_export", parentSubMenu = null))
                        }
                        "ก่อนส่งออกนอกราชอาณาจักร (สำหรับสินค้าน้ำมัน)" -> {
                            appState.navigateTo(Screen.PdfList("export_oil", parentSubMenu = null))
                        }
                        "ก่อนส่งออกนอกราชอาณาจักร (สำหรับสินค้าสุราและยาสูบ)" -> {
                            appState.navigateTo(Screen.PdfList("export_alcohol", parentSubMenu = null))
                        }
                        "ประวัติการยื่นคำขอย้อนหลัง 30 วัน" -> {
                            appState.navigateTo(Screen.HistoryList(screen, FilterData()))
                        }
                    }
                }
            )
        }

        is Screen.SubMenu -> {
            SubMenuScreen(
                onBack = {
                    appState.navigateToWithReplace(screen.parentRequest)
                },
                onItemClick = { itemId ->
                    appState.navigateTo(Screen.PdfList("approval", parentSubMenu = screen))
                }
            )
        }

        is Screen.ItemChoice -> {
            ItemChoiceScreen(
                sourceType = screen.sourceType,
                onBack = { appState.navigateBack() },
                onItemClick = { choice ->
                    when (choice.id) {
                        "images" -> {
                            appState.navigateTo(Screen.ImageList(
                                sourceType = screen.sourceType,
                                parentChoice = screen,
                                topicId = "default"
                            ))
                        }
                        "pdf" -> {
                            appState.navigateTo(Screen.PdfList(screen.sourceType, parentChoice = screen))
                        }
                        "transport" -> {
                            println("Transport option selected for ${screen.sourceType}")
                        }
                    }
                }
            )
        }

        is Screen.PdfList -> {
            PdfListScreen(
                sourceType = screen.sourceType,
                onBack = {
                    when {
                        screen.parentSubMenu != null -> {
                            appState.navigateToWithReplace(screen.parentSubMenu)
                        }
                        screen.parentChoice != null -> {
                            appState.navigateToWithReplace(screen.parentChoice)
                        }
                        else -> {
                            val requestScreen = when (screen.sourceType) {
                                "approval" -> {
                                    Screen.Request(
                                        fromCode = "PS28",
                                        signatureStatus = 1,
                                        dutyCodeFlag = null,
                                        historyFlag = false
                                    )
                                }
                                "before_export" -> {
                                    Screen.Request(
                                        fromCode = "PS28",
                                        signatureStatus = 3,
                                        dutyCodeFlag = null,
                                        historyFlag = false
                                    )
                                }
                                "export_oil" -> {
                                    Screen.Request(
                                        fromCode = "PS28",
                                        signatureStatus = 4,
                                        dutyCodeFlag = 1,
                                        historyFlag = false
                                    )
                                }
                                "export_alcohol" -> {
                                    Screen.Request(
                                        fromCode = "PS28",
                                        signatureStatus = 4,
                                        dutyCodeFlag = 2,
                                        historyFlag = false
                                    )
                                }
                                else -> {
                                    Screen.Request(
                                        fromCode = "PS28",
                                        signatureStatus = 1,
                                        dutyCodeFlag = null,
                                        historyFlag = false
                                    )
                                }
                            }
                            appState.navigateToWithReplace(requestScreen)
                        }
                    }
                },
                onItemClick = { pdf ->
                    if (screen.sourceType in listOf("before_export", "export_oil", "export_alcohol", "history")) {
                        appState.navigateTo(Screen.DocumentSelection(screen.sourceType, screen, pdf))
                    } else {
                        appState.navigateTo(Screen.PdfDetail(screen, pdf))
                    }
                }
            )
        }

        is Screen.PdfDetail -> {
            PdfDetailScreen(
                id = screen.selectedPdf.id,
                sourceType = screen.parentPdfList.sourceType,
                onBack = { appState.navigateBack() },
                onNext = { sourceType ->
                    when (sourceType) {
                        "single" -> {
                            appState.navigateTo(
                                Screen.Signature(
                                    sourceType = "single",
                                    parentPdfDetail = screen,
                                    parentInspection = null,
                                    topicId = screen.selectedPdf.id
                                )
                            )
                        }
                        "multiple" -> {
                            appState.navigateTo(
                                Screen.Inspection(
                                    sourceType = "multiple",
                                    parentPdfDetail = screen,
                                    topicId = screen.selectedPdf.id
                                )
                            )
                        }
                        "history" -> {
                            // TODO: implement history actions
                        }
                    }
                }
            )
        }

        is Screen.Inspection -> {
            InspectionScreen(
                topicId = screen.topicId,
                onBack = { appState.navigateBack() },
                onContinue = { topicId ->
                    appState.navigateTo(
                        Screen.Signature(
                            sourceType = "inspection",
                            parentPdfDetail = null,
                            parentInspection = screen,
                            topicId = topicId
                        )
                    )
                }
            )
        }

        is Screen.Signature -> {
            SignatureScreen(
                topicId = screen.topicId,
                sourceType = screen.sourceType,
                onBack = { appState.navigateBack() },
                onSubmit = { topicId ->
                    println("Signature submitted for Topic ID: $topicId")

                    when (screen.sourceType) {
                        "single" -> {
                            screen.parentPdfDetail?.let { pdfDetail ->
                                appState.navigateToWithReplace(pdfDetail.parentPdfList)
                            }
                        }
                        "inspection", "multiple" -> {
                            screen.parentInspection?.let { inspection ->
                                appState.navigateToWithReplace(inspection.parentPdfDetail.parentPdfList)
                            }
                        }
                        else -> {
                            appState.navigateBack()
                        }
                    }
                }
            )
        }

        is Screen.ImageList -> {
            ImageListScreen(
                onBack = { appState.navigateBack() },
                onAdd = { /* เรียกจาก bottom sheet แล้ว */ },
                onDeleteAll = {
                    println("Delete all images for topic: ${screen.topicId}")
                },
                onSave = {
                    println("Save images for topic: ${screen.topicId}")
                    appState.navigateBack()
                },
                imagePicker = imagePicker
            )
        }

        is Screen.HistoryList -> {
            HistoryListScreen(
                currentFilter = screen.currentFilter,
                onBack = { appState.navigateBack() },
                onItemClick = { history ->
                    val pdfItem = PdfItem(
                        id = history.id,
                        companyName = history.companyName,
                        receiptNumber = history.receiptNumber,
                        receivedDate = history.receivedDate,
                        status = history.status
                    )

                    val mockPdfList = PdfList(
                        sourceType = "history",
                        parentChoice = null,
                        parentSubMenu = null
                    )

                    appState.navigateTo(Screen.DocumentSelection("history", mockPdfList, pdfItem))
                },
                onFilterClick = {
                    appState.navigateTo(Screen.DateFilter(screen))
                }
            )
        }

        is Screen.DateFilter -> {
            DateFilterScreen(
                currentFilter = screen.currentFilter,
                onBack = { appState.navigateBack() },
                onApplyFilter = { filterData ->
                    val updatedHistoryScreen = screen.parentHistory.copy(currentFilter = filterData)
                    appState.navigateToWithReplace(updatedHistoryScreen)
                },
                onClearFilter = {
                    val clearedHistoryScreen = screen.parentHistory.copy(currentFilter = FilterData())
                    appState.navigateToWithReplace(clearedHistoryScreen)
                }
            )
        }

        is Screen.ExemptionList -> {
            ExemptionListScreen(
                onBack = { appState.navigateBack() }
            )
        }

        is Screen.QrScanner -> {
            QrScannerView(
                onBack = { appState.navigateBack() }
            )
        }

        is Screen.DocumentSelection -> {
            DocumentSelectionScreen(
                sourceType = screen.sourceType,
                onBack = { appState.navigateBack() },
                onItemClick = { item ->
                    when (item.id) {
                        "details" -> {
                            appState.navigateTo(Screen.PdfDetail(screen.parentPdfList, screen.selectedPdf))
                        }
                        "images" -> {
                            appState.navigateTo(Screen.ImageList(
                                sourceType = screen.sourceType,
                                parentChoice = ItemChoice(screen.sourceType, parentSubMenu = null),
                                topicId = screen.selectedPdf.id
                            ))
                        }
                        "transport" -> {
                            appState.navigateTo(Screen.TransportDocument(
                                sourceType = screen.sourceType,
                                parentDocumentSelection = screen,
                                topicId = screen.selectedPdf.id
                            ))
                        }
                    }
                }
            )
        }

        is Screen.TransportDocument -> {
            TransportDocumentScreen(
                topicId = screen.topicId,
                sourceType = screen.sourceType,
                onBack = { appState.navigateBack() },
                onDocumentClick = { document ->
                    println("Selected transport document: ${document.setNumber}")
                }
            )
        }
    }
}