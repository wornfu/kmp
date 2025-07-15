// androidApp/src/main/kotlin/com/jetbrains/kmpapp/iSmart_Audit.kt
package com.jetbrains.kmpapp

import android.app.Application
import com.jetbrains.kmpapp.di.initKoin

class iSmartAudit2 : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
