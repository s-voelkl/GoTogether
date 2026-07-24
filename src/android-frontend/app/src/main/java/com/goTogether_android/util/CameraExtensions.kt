package com.goTogether_android.util

import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

fun MapLibreMap.flyTo(lat: Double, lng: Double, zoom: Double, duration: Int = 700) {
    val camera = CameraPosition.Builder()
        .target(LatLng(lat, lng))
        .zoom(zoom)
        .build()

    animateCamera(CameraUpdateFactory.newCameraPosition(camera), duration)
}
