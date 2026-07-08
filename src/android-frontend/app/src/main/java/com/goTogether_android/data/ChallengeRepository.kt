package com.goTogether_android.data

/**
 * Repository for managing and accessing [Challenge] data.
 * Currently provides mock data for development.
 */
object ChallengeRepository {

    /**
     * A list of predefined challenges for demonstration purposes.
     */
    val mockChallenges = listOf(
        Challenge(
            id = "1", name = "Café Baroco", category = "Food",
            lat = 49.4452, lng = 11.8572, points = 150,
            description = "Visit the historic Café Baroco in the old town and try their signature cake.",
            host = "Café Baroco GmbH", startTime = "2026-06-06T15:00:00",
            durationMinutes = 90, experiencePoints = 150, minSocialBattery = 40,
            maxPlayers = 0, participants = 12, verificationCode = "K7M2Q"
        ),
        Challenge(
            id = "2", name = "TV 1861 Amberg", category = "Sport",
            lat = 49.4389, lng = 11.8698, points = 300,
            description = "Attend a home match at the TV 1861 Amberg sports club.",
            host = "TV 1861 Amberg e.V.", startTime = "2026-06-07T16:30:00",
            durationMinutes = 120, experiencePoints = 300, minSocialBattery = 80,
            maxPlayers = 30, participants = 30, verificationCode = "H5J1W"
        ),
        Challenge(
            id = "3", name = "Stadtmuseum", category = "Culture",
            lat = 49.4431, lng = 11.8615, points = 200,
            description = "Explore Amberg's city museum and discover 1000 years of local history.",
            host = "Stadt Amberg", startTime = "2026-06-08T10:00:00",
            durationMinutes = 60, experiencePoints = 200, minSocialBattery = 40,
            maxPlayers = 25, participants = 9, verificationCode = "R3T8N"
        ),
        Challenge(
            id = "4", name = "Vils Promenade", category = "Nature",
            lat = 49.4478, lng = 11.8602, points = 100,
            description = "Take a 3 km walk along the scenic Vils river promenade.",
            host = "Amberg Tourismus", startTime = "2026-06-06T09:00:00",
            durationMinutes = 45, experiencePoints = 100, minSocialBattery = 20,
            maxPlayers = 0, participants = 5, verificationCode = "P9X4L"
        ),
        Challenge(
            id = "5", name = "Marktplatz Brunch", category = "Social",
            lat = 49.4461, lng = 11.8630, points = 250,
            description = "Meet up with locals for Sunday brunch at the market square.",
            host = "Local Friends Amberg", startTime = "2026-06-08T11:00:00",
            durationMinutes = 120, experiencePoints = 250, minSocialBattery = 60,
            maxPlayers = 12, participants = 12, verificationCode = "B2D6V"
        ),
        Challenge(
            id = "6", name = "Kurfürstenbad", category = "Sport",
            lat = 49.4410, lng = 11.8550, points = 200,
            description = "Do 10 laps at the historic Kurfürstenbad indoor swimming pool.",
            host = "Kurfürstenbad Amberg", startTime = "2026-06-09T18:00:00",
            durationMinutes = 60, experiencePoints = 200, minSocialBattery = 60,
            maxPlayers = 20, participants = 14, verificationCode = "M8K3Z"
        )
    )

    /**
     * Finds a challenge by its verification code.
     *
     * @param code The code to search for (case-insensitive).
     * @return The matching [Challenge] if found, null otherwise.
     */
    fun findByVerificationCode(code: String): Challenge? {
        val normalized = code.trim().uppercase()
        return mockChallenges.find { it.verificationCode == normalized }
    }

    /**
     * Finds a challenge by its unique ID.
     *
     * @param id The ID to search for.
     * @return The matching [Challenge] if found, null otherwise.
     */
    fun findById(id: String): Challenge? {
        return mockChallenges.find { it.id == id }
    }
}
