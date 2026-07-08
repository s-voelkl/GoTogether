package com.goTogether_android.data

/**
 * Represents the difficulty level of a challenge based on social battery requirements.
 */
enum class Difficulty {
    EASY, MEDIUM, HARD
}

/**
 * Data model for a Challenge in the GoTogether app.
 *
 * @property id Unique identifier for the challenge.
 * @property name Display name of the challenge.
 * @property category The category this challenge belongs to (e.g., Food, Sport).
 * @property lat Latitude coordinate.
 * @property lng Longitude coordinate.
 * @property points Reward points for completion.
 * @property description Detailed description of the activity.
 * @property host The organization or entity hosting the challenge.
 * @property startTime ISO format start time.
 * @property durationMinutes Estimated duration of the activity.
 * @property experiencePoints Leveling experience gained.
 * @property minSocialBattery Required minimum social energy (0-100).
 * @property maxPlayers Maximum number of participants (0 for unlimited).
 * @property participants Current number of joined users.
 * @property verificationCode Unique code used for check-in.
 */
data class Challenge(
    val id: String,
    val name: String,
    val category: String,
    val lat: Double,
    val lng: Double,
    val points: Int,
    val description: String,
    val host: String,
    val startTime: String,
    val durationMinutes: Int,
    val experiencePoints: Int,
    val minSocialBattery: Int,
    val maxPlayers: Int,
    val participants: Int,
    val verificationCode: String
)

/**
 * Extension function to determine the difficulty based on the social battery requirement.
 */
fun Challenge.getDifficulty(): Difficulty {
    return when {
        minSocialBattery <= 33 -> Difficulty.EASY
        minSocialBattery <= 66 -> Difficulty.MEDIUM
        else -> Difficulty.HARD
    }
}

/**
 * Extension function to check if the challenge has reached its maximum capacity.
 */
fun Challenge.isFull(): Boolean {
    return maxPlayers != 0 && participants >= maxPlayers
}

/**
 * Information about a challenge category for UI display.
 */
data class CategoryInfo(val id: String, val label: String, val emoji: String, val color: String)

/**
 * Global configuration for filter categories.
 */
val FILTER_CATEGORIES = listOf(
    CategoryInfo("Food", "Food", "☕", "#FF6B6B"),
    CategoryInfo("Sport", "Sport", "⚽", "#4ECDC4"),
    CategoryInfo("Culture", "Culture", "🏛️", "#A78BFA"),
    CategoryInfo("Nature", "Nature", "🌿", "#10B981"),
    CategoryInfo("Social", "Social", "🎉", "#F59E0B")
)

/**
 * Map of difficulty levels to their associated UI colors.
 */
val DIFFICULTY_COLORS = mapOf(
    Difficulty.EASY to "#10B981",
    Difficulty.MEDIUM to "#F59E0B",
    Difficulty.HARD to "#EF4444"
)
