package com.goTogether_android.util

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import com.goTogether_android.R
import com.goTogether_android.data.*

/**
 * Utility functions for setting up common UI components.
 */

/**
 * Populates a challenge card view with data from a [Challenge] object.
 *
 * @param view The root view of the challenge card.
 * @param challenge The challenge data to display.
 */
fun setupChallengeCard(view: View, challenge: Challenge) {
    val nameText = view.findViewById<TextView>(R.id.challengeName)
    val categoryText = view.findViewById<TextView>(R.id.challengeCategory)
    val pointsText = view.findViewById<TextView>(R.id.challengePoints)
    val emojiText = view.findViewById<TextView>(R.id.challengeEmoji)
    val iconBox = view.findViewById<View>(R.id.iconBox)
    val diffBadge = view.findViewById<TextView>(R.id.difficultyBadge)
    val fullBadge = view.findViewById<TextView>(R.id.fullBadge)
    val container = view.findViewById<View>(R.id.cardContainer)

    // Basic text data
    nameText.text = challenge.name
    categoryText.text = challenge.category
    pointsText.text = view.context.getString(R.string.points_format, challenge.points)
    
    // Category specific UI
    val catInfo = FILTER_CATEGORIES.find { it.id == challenge.category }
    emojiText.text = catInfo?.emoji ?: "📍"
    
    // Set icon box background with low opacity accent color
    val accentColor = Color.parseColor(catInfo?.color ?: "#6B7280")
    applyStyledBackground(iconBox, accentColor, 40, 14f)

    // Difficulty badge setup
    val difficulty = challenge.getDifficulty()
    val diffColorStr = DIFFICULTY_COLORS[difficulty] ?: "#6B7280"
    val diffColor = Color.parseColor(diffColorStr)
    
    diffBadge.text = difficulty.name.lowercase()
    diffBadge.setTextColor(diffColor)
    applyStyledBackground(diffBadge, diffColor, 34, 100f)

    // Handle full state
    val isFull = challenge.isFull()
    fullBadge.visibility = if (isFull) View.VISIBLE else View.GONE
    diffBadge.visibility = if (isFull) View.GONE else View.VISIBLE
    
    // Reduce opacity if the challenge is full/inactive
    container.alpha = if (isFull) 0.45f else 1.0f
}

/**
 * Helper to apply a tinted background with rounded corners to a view.
 */
private fun applyStyledBackground(view: View, color: Int, alpha: Int, cornerRadiusDp: Float) {
    val alphaColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    val density = view.context.resources.displayMetrics.density
    
    val bg = view.background as? GradientDrawable ?: GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
    }
    
    bg.cornerRadius = cornerRadiusDp * density
    bg.setColor(alphaColor)
    view.background = bg
}
