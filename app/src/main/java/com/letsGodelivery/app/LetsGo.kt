package com.letsGodelivery.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LetsGo: Application() {
    // You can add onCreate logic here later if your application needs
    // to perform tasks when it's first created.
    // For Hilt's basic setup, this is often empty.
    override fun onCreate() {
        super.onCreate()
        // Initialize other app-wide libraries here if needed
    }
}