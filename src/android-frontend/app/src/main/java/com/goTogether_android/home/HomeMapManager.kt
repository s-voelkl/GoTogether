package com.goTogether_android.home

import android.graphics.Color
import com.goTogether_android.data.Challenge
import com.goTogether_android.data.ChallengeRepository
import com.goTogether_android.data.FILTER_CATEGORIES
import com.goTogether_android.util.MarkerGenerator
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

/**
 * Manages MapLibre map interactions for the Home screen.
 * Handles marker rendering, selection, and camera movements.
 */
class HomeMapManager(
    private val mapView: MapView,
    private val onChallengeSelected: (Challenge) -> Unit
) {
    private var map: MapLibreMap? = null
    private val markerToChallengeId = mutableMapOf<Long, String>()
    private var selectedMarker: Marker? = null

    companion object {
        val AMBERG_CENTER = LatLng(49.4452, 11.8572)
        const val DEFAULT_ZOOM = 13.0
        const val CHALLENGE_ZOOM = 15.5
    }

    /**
     * Initializes the map and sets up listeners.
     */
    fun setup(mapLibreMap: MapLibreMap) {
        this.map = mapLibreMap
        
        mapLibreMap.uiSettings.isRotateGesturesEnabled = false
        mapLibreMap.uiSettings.isTiltGesturesEnabled = false

        mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AMBERG_CENTER, DEFAULT_ZOOM))

        mapLibreMap.setOnMarkerClickListener { marker ->
            val id = markerToChallengeId[marker.id]
            val challenge = ChallengeRepository.findById(id ?: "")
            if (challenge != null) {
                selectMarker(marker)
                onChallengeSelected(challenge)
            }
            true
        }
    }

    /**
     * Renders a list of challenge markers on the map.
     */
    fun renderMarkers(challenges: List<Challenge>) {
        val currentMap = map ?: return
        currentMap.clear()
        markerToChallengeId.clear()

        challenges.forEach { challenge ->
            val cat = FILTER_CATEGORIES.find { it.id == challenge.category }
            val color = Color.parseColor(cat?.color ?: "#6B7280")
            val iconBitmap = MarkerGenerator.generateMarkerIcon(
                mapView.context,
                color,
                cat?.emoji ?: "📍",
                false
            )
            
            val marker = currentMap.addMarker(MarkerOptions()
                .position(LatLng(challenge.lat, challenge.lng))
                .icon(IconFactory.getInstance(mapView.context).fromBitmap(iconBitmap))
            )
            markerToChallengeId[marker.id] = challenge.id
        }
    }

    /**
     * Focuses the camera on a specific challenge and highlights its marker.
     */
    fun focusChallenge(challenge: Challenge) {
        val currentMap = map ?: return
        
        // Deselect previous
        selectedMarker?.let { prev ->
            val prevChallengeId = markerToChallengeId[prev.id]
            val prevChallenge = ChallengeRepository.findById(prevChallengeId ?: "")
            if (prevChallenge != null) {
                updateMarkerIcon(prev, prevChallenge, false)
            }
        }

        // Find and select new marker
        val newMarkerId = markerToChallengeId.entries.find { it.value == challenge.id }?.key
        val newMarker = currentMap.markers.find { it.id == newMarkerId }
        
        if (newMarker != null) {
            selectMarker(newMarker)
            updateMarkerIcon(newMarker, challenge, true)
        }

        val pos = CameraPosition.Builder()
            .target(LatLng(challenge.lat, challenge.lng))
            .zoom(CHALLENGE_ZOOM)
            .build()
        currentMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 800)
    }

    private fun selectMarker(marker: Marker) {
        selectedMarker = marker
    }

    private fun updateMarkerIcon(marker: Marker, challenge: Challenge, isSelected: Boolean) {
        val cat = FILTER_CATEGORIES.find { it.id == challenge.category }
        val color = Color.parseColor(cat?.color ?: "#6B7280")
        val iconBitmap = MarkerGenerator.generateMarkerIcon(
            mapView.context,
            color,
            cat?.emoji ?: "📍",
            isSelected
        )
        marker.icon = IconFactory.getInstance(mapView.context).fromBitmap(iconBitmap)
    }
}
