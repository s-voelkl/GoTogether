package svoelkl2.mauc.androidtest01.ui.quests

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val reward: String,
    val type: String,
    val duration: String = "1h", // New attribute
    val minBattery: Int = 0,
    val tags: List<String> = emptyList(),
    val xpValue: Int = 10,
    val currencyValue: Int = 5,
    val latitude: Double,
    val longitude: Double,
    val participants: List<String> = emptyList(),
    var isDone: Boolean = false
)