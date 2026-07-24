package com.goTogether_android.data

enum class Difficulty {
    EASY, MEDIUM, HARD
}

data class Challenge(
    val id: String,
    val name: String,
    val category: String,
    val lat: Double,
    val lng: Double,
    val points: Int,
    val description: String,
    val host: String,
    val startTime: String, // ISO
    val durationMinutes: Int,
    val experiencePoints: Int,
    val minSocialBattery: Int, // 1–5 (difficulty is derived from this)
    val maxPlayers: Int, // 0 = unlimited
    val participants: Int,
    val verificationCode: String
)

fun Challenge.getDifficulty(): Difficulty {
    return when {
        minSocialBattery <= 2 -> Difficulty.EASY
        minSocialBattery == 3 -> Difficulty.MEDIUM
        else -> Difficulty.HARD
    }
}

fun Challenge.isFull(): Boolean {
    return maxPlayers != 0 && participants >= maxPlayers
}

val mockChallenges = listOf(
    Challenge(
        id = "1", name = "Café Baroco", category = "Food",
        lat = 49.4452, lng = 11.8572, points = 150,
        description = "Visit the historic Café Baroco in the old town and try their signature cake.",
        host = "Café Baroco GmbH", startTime = "2026-06-06T15:00:00",
        durationMinutes = 90, experiencePoints = 150, minSocialBattery = 2,
        maxPlayers = 0, participants = 12, verificationCode = "K7M2Q"
    ),
    Challenge(
        id = "2", name = "TV 1861 Amberg", category = "Sport",
        lat = 49.4389, lng = 11.8698, points = 300,
        description = "Attend a home match at the TV 1861 Amberg sports club.",
        host = "TV 1861 Amberg e.V.", startTime = "2026-06-07T16:30:00",
        durationMinutes = 120, experiencePoints = 300, minSocialBattery = 4,
        maxPlayers = 30, participants = 30, verificationCode = "H5J1W"
    ),
    Challenge(
        id = "3", name = "Stadtmuseum", category = "Culture",
        lat = 49.4431, lng = 11.8615, points = 200,
        description = "Explore Amberg's city museum and discover 1000 years of local history.",
        host = "Stadt Amberg", startTime = "2026-06-08T10:00:00",
        durationMinutes = 60, experiencePoints = 200, minSocialBattery = 2,
        maxPlayers = 25, participants = 9, verificationCode = "R3T8N"
    ),
    Challenge(
        id = "4", name = "Vils Promenade", category = "Nature",
        lat = 49.4478, lng = 11.8602, points = 100,
        description = "Take a 3 km walk along the scenic Vils river promenade.",
        host = "Amberg Tourismus", startTime = "2026-06-06T09:00:00",
        durationMinutes = 45, experiencePoints = 100, minSocialBattery = 1,
        maxPlayers = 0, participants = 5, verificationCode = "P9X4L"
    ),
    Challenge(
        id = "5", name = "Marktplatz Brunch", category = "Social",
        lat = 49.4461, lng = 11.8630, points = 250,
        description = "Meet up with locals for Sunday brunch at the market square.",
        host = "Local Friends Amberg", startTime = "2026-06-08T11:00:00",
        durationMinutes = 120, experiencePoints = 250, minSocialBattery = 3,
        maxPlayers = 12, participants = 12, verificationCode = "B2D6V"
    ),
    Challenge(
        id = "6", name = "Kurfürstenbad", category = "Sport",
        lat = 49.4410, lng = 11.8550, points = 200,
        description = "Do 10 laps at the historic Kurfürstenbad indoor swimming pool.",
        host = "Kurfürstenbad Amberg", startTime = "2026-06-09T18:00:00",
        durationMinutes = 60, experiencePoints = 200, minSocialBattery = 3,
        maxPlayers = 20, participants = 14, verificationCode = "M8K3Z"
    )
)

fun findChallengeByCode(code: String): Challenge? {
    val v = code.trim().uppercase()
    return mockChallenges.find { it.verificationCode == v }
}

val FILTER_CATEGORIES = listOf(
    CategoryInfo("Food", "Food", "☕", "#FF6B6B"),
    CategoryInfo("Sport", "Sport", "⚽", "#4ECDC4"),
    CategoryInfo("Culture", "Culture", "🏛️", "#A78BFA"),
    CategoryInfo("Nature", "Nature", "🌿", "#10B981"),
    CategoryInfo("Social", "Social", "🎉", "#F59E0B")
)

data class CategoryInfo(val id: String, val label: String, val emoji: String, val color: String)

val DIFFICULTY_COLORS = mapOf(
    Difficulty.EASY to "#10B981",
    Difficulty.MEDIUM to "#F59E0B",
    Difficulty.HARD to "#EF4444"
)
