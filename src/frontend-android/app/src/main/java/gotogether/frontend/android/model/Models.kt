package gotogether.frontend.android.model

import java.util.UUID

data class User(
    val id: UUID,
    val name: String,
    val email: String,
    val socialBattery: Int,
    val currency: Int,
    val level: Int,
    val levelXp: Int,
    val interests: List<UUID>,
    val lastLogin: String?,
    val settings: Settings
)

data class Settings(
    val setting: String = "default"
)

data class Company(
    val id: UUID,
    val name: String,
    val email: String,
    val currency: Int,
    val street: String,
    val houseNumber: String,
    val zipCode: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)

data class Challenge(
    val id: UUID,
    val title: String,
    val description: String,
    val isArchived: Boolean,
    val startTime: String,
    val durationMinutes: Int,
    val latitude: Double,
    val longitude: Double,
    val currency: Int,
    val experiencePoints: Int,
    val minSocialBattery: Int,
    val maxPlayers: Int,
    val currentPlayers: Int,
    val topicIds: List<UUID>,
    val hostCompanyName: String
)

data class Topic(
    val id: UUID,
    val name: String,
    val icon: String? = null,
    val backgroundColor: String? = null
)

data class Location(
    val latitude: Double,
    val longitude: Double
)
