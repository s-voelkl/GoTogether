package com.goTogether_android

import android.app.Application
import org.maplibre.android.MapLibre

class GoTogetherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
    }
}
