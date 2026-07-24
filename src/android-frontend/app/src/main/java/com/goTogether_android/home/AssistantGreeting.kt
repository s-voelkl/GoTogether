package com.goTogether_android.home

import com.goTogether_android.data.Challenge

data class AssistantGreeting(
    val message: String,
    val matchedInterestIds: List<String>,
    val suggestedChallenge: Challenge?
)
