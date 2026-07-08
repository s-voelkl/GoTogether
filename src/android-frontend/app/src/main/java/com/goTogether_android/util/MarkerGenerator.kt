package com.goTogether_android.util

import android.content.Context
import android.graphics.*
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory

/**
 * Utility for generating custom map marker icons programmatically.
 */
object MarkerGenerator {

    /**
     * Generates a circular marker icon with a specific color and optional selection state.
     *
     * @param context Android context.
     * @param color The main color of the marker.
     * @param emoji The emoji to display inside (not currently used in this version).
     * @param isSelected Whether the marker should be rendered in a selected/highlighted state.
     * @return An [Icon] that can be used with MapLibre.
     */
    fun generateMarkerIcon(
        context: Context,
        color: Int,
        emoji: String,
        isSelected: Boolean
    ): Bitmap {
        val density = context.resources.displayMetrics.density
        val sizePx = if (isSelected) (40 * density).toInt() else (28 * density).toInt()

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val centerX = sizePx / 2f
        val centerY = sizePx / 2f

        if (isSelected) {
            // Outer black ring
            paint.color = Color.BLACK
            canvas.drawCircle(centerX, centerY, sizePx / 2f, paint)
            
            // White inner ring
            paint.color = Color.WHITE
            canvas.drawCircle(centerX, centerY, (sizePx / 2f) - (2 * density), paint)

            // Color fill
            paint.color = color
            canvas.drawCircle(centerX, centerY, (sizePx / 2f) - (4 * density), paint)
        } else {
            // Simple white border
            paint.color = Color.WHITE
            canvas.drawCircle(centerX, centerY, sizePx / 2f, paint)

            // Color fill
            paint.color = color
            canvas.drawCircle(centerX, centerY, (sizePx / 2f) - (2 * density), paint)
        }

        return bitmap
    }
}
