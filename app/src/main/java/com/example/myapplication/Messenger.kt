package com.example.myapplication

import android.app.Application
import com.example.myapplication.utils.TokenManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}