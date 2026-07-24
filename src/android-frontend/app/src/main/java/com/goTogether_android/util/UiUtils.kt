package com.goTogether_android.util

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.goTogether_android.R
import com.goTogether_android.data.*

fun setupChallengeCard(view: View, challenge: Challenge) {
    val name = view.findViewById<TextView>(R.id.challengeName)
    val category = view.findViewById<TextView>(R.id.challengeCategory)
    val points = view.findViewById<TextView>(R.id.challengePoints)
    val emoji = view.findViewById<TextView>(R.id.challengeEmoji)
    val iconBox = view.findViewById<View>(R.id.iconBox)
    val diffBadge = view.findViewById<TextView>(R.id.difficultyBadge)
    val fullBadge = view.findViewById<TextView>(R.id.fullBadge)
    val container = view.findViewById<View>(R.id.cardContainer)

    name.text = challenge.name
    category.text = challenge.category
    points.text = "${challenge.points} pts"
    
    val catInfo = FILTER_CATEGORIES.find { it.id == challenge.category }
    emoji.text = catInfo?.emoji ?: "📍"
    
    // Set icon box background with 16% opacity (hex 28)
    val accentColor = Color.parseColor(catInfo?.color ?: "#6B7280")
    val alphaColor = Color.argb(40, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor))
    val bg = iconBox.background as? GradientDrawable ?: GradientDrawable().apply {
        cornerRadius = view.context.resources.displayMetrics.density * 14
    }
    bg.setColor(alphaColor)
    iconBox.background = bg

    val difficulty = challenge.getDifficulty()
    val diffColorStr = DIFFICULTY_COLORS[difficulty] ?: "#6B7280"
    val diffColor = Color.parseColor(diffColorStr)
    
    diffBadge.text = difficulty.name.lowercase()
    diffBadge.setTextColor(diffColor)
    
    val diffAlphaColor = Color.argb(34, Color.red(diffColor), Color.green(diffColor), Color.blue(diffColor))
    val diffBg = GradientDrawable().apply {
        cornerRadius = view.context.resources.displayMetrics.density * 100
        setColor(diffAlphaColor)
    }
    diffBadge.background = diffBg

    val isFull = challenge.isFull()
    fullBadge.visibility = if (isFull) View.VISIBLE else View.GONE
    diffBadge.visibility = if (isFull) View.GONE else View.VISIBLE
    
    container.alpha = if (isFull) 0.45f else 1.0f
}
