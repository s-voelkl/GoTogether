package com.goTogether_android.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.TimeUnit

/**
 * Provides access to the user's current physical location using Google Play Services.
 *
 * @property context The application or activity context.
 */
class UserLocationProvider(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Attempts to retrieve the last known location of the device.
     * This is a blocking call and should not be run on the UI thread.
     *
     * @return The [Location] object if successful, or null if location is unavailable or timeout occurs.
     */
    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        return try {
            val task = fusedLocationClient.lastLocation
            // Wait up to 2 seconds for the location task to complete.
            Tasks.await(task, 2, TimeUnit.SECONDS)
        } catch (e: Exception) {
            Log.e("UserLocationProvider", "Failed to get location: ${e.message}")
            null
        }
    }
}
