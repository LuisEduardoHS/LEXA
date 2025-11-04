package com.lexa.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// 1. Le decimos a Hilt que esta es la clase principal
@HiltAndroidApp
class LexaApplication : Application() {
    override fun onCreate(){
        super.onCreate()
    }
}