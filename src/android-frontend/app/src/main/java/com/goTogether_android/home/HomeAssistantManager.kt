package com.goTogether_android.home

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.goTogether_android.R
import com.goTogether_android.challenges.ChallengeDetailBottomSheet
import com.goTogether_android.data.ChallengeRepository

/**
 * Manages the AI Assistant greeting card on the Home screen.
 * Handles the logic for suggesting challenges based on the user's social battery.
 */
class HomeAssistantManager(
    private val cardView: View,
    private val fragmentManager: FragmentManager
) {
    private val greetingHandler = Handler(Looper.getMainLooper())
    private val hideGreetingRunnable = Runnable { cardView.visibility = View.GONE }

    /**
     * Initializes and displays the AI greeting if suitable challenges are found.
     */
    fun setupAiGreeting() {
        val context = cardView.context
        val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        val battery = prefs.getInt("saved_battery", 60)
        
        // Find challenges that fit the user's current social battery
        val suitable = ChallengeRepository.mockChallenges.filter { it.minSocialBattery <= battery }
        if (suitable.isEmpty()) return
        
        val challenge = suitable.random()
        
        val textView = cardView.findViewById<TextView>(R.id.aiGreetingText)
        textView.text = context.getString(R.string.ai_greeting_format, battery, challenge.name)
        
        cardView.visibility = View.VISIBLE
        
        // Set up button actions
        cardView.findViewById<View>(R.id.aiOkBtn).setOnClickListener {
            hideGreeting()
        }
        
        cardView.findViewById<View>(R.id.aiSeeChallengeBtn).setOnClickListener {
            hideGreeting()
            ChallengeDetailBottomSheet.newInstance(challenge.id).show(fragmentManager, "detail")
        }
        
        // Automatically hide the greeting after 10 seconds
        greetingHandler.postDelayed(hideGreetingRunnable, 10000)
    }

    /**
     * Hides the greeting card and cancels any pending hide tasks.
     */
    fun hideGreeting() {
        cardView.visibility = View.GONE
        greetingHandler.removeCallbacks(hideGreetingRunnable)
    }

    /**
     * Cleanup resources.
     */
    fun onDestroy() {
        greetingHandler.removeCallbacks(hideGreetingRunnable)
    }
}
