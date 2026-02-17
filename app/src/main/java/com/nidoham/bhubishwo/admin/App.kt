package com.nidoham.bhubishwo.admin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        const val IMGBB_API_KEY = "143e896bb456960f76ee6c94b760f1bf"
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}