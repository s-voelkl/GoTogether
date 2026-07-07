package svoelkl2.mauc.androidtest01.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 0,
    val interests: String = "", // Comma-separated
    val selectedTeam: String = "None",
    val socialBattery: Int = 50,
    val socialXP: Int = 0
)