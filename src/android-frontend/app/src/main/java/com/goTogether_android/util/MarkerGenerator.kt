package com.goTogether_android.util

import android.content.Context
import android.graphics.*
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory

object MarkerGenerator {

    fun generate(context: Context, colorHex: String, isSelected: Boolean): Icon {
        val density = context.resources.displayMetrics.density
        val sizePx = if (isSelected) (32 * density).toInt() else (22 * density).toInt()
        val color = Color.parseColor(colorHex)

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val centerX = sizePx / 2f
        val centerY = sizePx / 2f

        if (isSelected) {
            // Outer black stroke
            paint.color = Color.BLACK
            canvas.drawCircle(centerX, centerY, sizePx / 2f, paint)
            
            // White inner ring
            paint.color = Color.WHITE
            canvas.drawCircle(centerX, centerY, (sizePx / 2f) - (2 * density), paint)

            // Main color circle
            paint.color = color
            canvas.drawCircle(centerX, centerY, (sizePx / 2f) - (5 * density), paint)
        } else {
            // White outline for better contrast
            paint.color = Color.WHITE
            canvas.drawCircle(centerX, centerY, sizePx / 2f, paint)

            // Main color circle
            paint.color = color
            canvas.drawCircle(centerX, centerY, (sizePx / 2f) - (2 * density), paint)
        }

        return IconFactory.getInstance(context).fromBitmap(bitmap)
    }
}
