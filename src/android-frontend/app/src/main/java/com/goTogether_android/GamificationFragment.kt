package com.goTogether_android

import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.goTogether_android.data.*
import java.text.SimpleDateFormat
import java.util.*

class GamificationFragment : Fragment() {

    private val LEVEL_XP_STEP = 250

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gamification, container, false)

        val recentChallenges = mockChallenges.take(4)
        val totalXP = recentChallenges.sumOf { it.experiencePoints }
        val totalGbucks = recentChallenges.sumOf { it.points }
        
        val level = (totalXP / LEVEL_XP_STEP) + 1
        val levelXp = totalXP % LEVEL_XP_STEP
        val progress = levelXp.toFloat() / LEVEL_XP_STEP

        // Hero Info
        view.findViewById<TextView>(R.id.levelText).text = "Level $level"
        view.findViewById<TextView>(R.id.xpSubText).text = "$totalXP XP earned from recent challenges"
        view.findViewById<TextView>(R.id.xpProgressText).text = "$levelXp/$LEVEL_XP_STEP XP this level"
        view.findViewById<TextView>(R.id.nextLevelText).text = "Next: Level ${level + 1}"
        
        setupProgressBar(view.findViewById(R.id.progressTrack), progress)

        // Metrics
        view.findViewById<TextView>(R.id.gbucksValue).text = totalGbucks.toString()
        
        val rewards = listOf(
            Reward("coffee-drop", "CD", "Coffee Drop", "Collect 200 G-Bucks", "#FFB800", totalGbucks >= 200),
            Reward("fast-track", "FT", "Fast Track Pass", "Reach level 4", "#60A5FA", level >= 4),
            Reward("city-legend", "CL", "City Legend Badge", "Complete 5 challenges", "#E5E7EB", recentChallenges.size >= 5)
        )
        
        val unlockedCount = rewards.count { it.unlocked }
        view.findViewById<TextView>(R.id.rewardsSummary).text = "$unlockedCount/${rewards.size}"
        view.findViewById<TextView>(R.id.liveRewardsChip).text = "$unlockedCount live"

        // Lists
        val rewardsList = view.findViewById<LinearLayout>(R.id.rewardsList)
        rewards.forEachIndexed { index, reward ->
            if (index > 0) addDivider(rewardsList)
            addRewardItem(rewardsList, reward)
        }

        val historyList = view.findViewById<LinearLayout>(R.id.historyList)
        recentChallenges.forEachIndexed { index, challenge ->
            if (index > 0) addDivider(historyList)
            addHistoryItem(historyList, challenge)
        }

        return view
    }

    private fun setupProgressBar(track: View, progress: Float) {
        val density = resources.displayMetrics.density
        val bg = GradientDrawable().apply {
            cornerRadius = 100 * density
            setColor(Color.parseColor("#8CFFFFFF"))
            setStroke(resources.getDimensionPixelSize(R.dimen.border_width), Color.BLACK)
        }
        
        val fg = GradientDrawable().apply {
            cornerRadius = 100 * density
            setColor(Color.BLACK)
        }
        val clip = ClipDrawable(fg, Gravity.START, ClipDrawable.HORIZONTAL)
        clip.level = (progress * 10000).toInt()
        
        val layer = LayerDrawable(arrayOf(bg, clip))
        track.background = layer
    }

    private fun addRewardItem(parent: LinearLayout, reward: Reward) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_reward, parent, false)
        view.findViewById<TextView>(R.id.rewardBadgeText).text = reward.label
        view.findViewById<TextView>(R.id.rewardTitle).text = reward.title
        view.findViewById<TextView>(R.id.rewardSubtitle).text = reward.subtitle
        
        val badge = view.findViewById<MaterialCardView>(R.id.rewardBadge)
        badge.setCardBackgroundColor(Color.parseColor(reward.color))
        
        val status = view.findViewById<TextView>(R.id.rewardStatus)
        status.text = if (reward.unlocked) "Unlocked" else "Locked"
        if (reward.unlocked) {
            status.setBackgroundResource(R.drawable.bg_status_chip_ready)
            status.setTextColor(Color.BLACK)
        }
        
        parent.addView(view)
    }

    private fun addHistoryItem(parent: LinearLayout, challenge: Challenge) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.item_history, parent, false)
        view.findViewById<TextView>(R.id.historyTitle).text = challenge.name
        
        val date = SimpleDateFormat("dd MMM", Locale.UK).format(Date()) // Placeholder date
        view.findViewById<TextView>(R.id.historySubtitle).text = "${challenge.category} / $date"
        view.findViewById<TextView>(R.id.historyXP).text = "+${challenge.experiencePoints} XP"
        view.findViewById<TextView>(R.id.historyPoints).text = "+${challenge.points} G"
        
        val emoji = view.findViewById<TextView>(R.id.historyEmoji)
        val catInfo = FILTER_CATEGORIES.find { it.id == challenge.category }
        emoji.text = catInfo?.emoji ?: "📍"
        
        val iconBox = view.findViewById<View>(R.id.historyIconBox)
        val accentColor = Color.parseColor(catInfo?.color ?: "#60A5FA")
        val alphaColor = Color.argb(40, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor))
        val bg = GradientDrawable().apply {
            cornerRadius = resources.displayMetrics.density * 16
            setColor(alphaColor)
        }
        iconBox.background = bg
        
        parent.addView(view)
    }

    private fun addDivider(parent: LinearLayout) {
        val divider = View(requireContext())
        divider.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1).apply {
            setMargins(0, 0, 0, 0)
        }
        divider.setBackgroundColor(Color.parseColor("#E5E7EB"))
        parent.addView(divider)
    }

    data class Reward(val id: String, val label: String, val title: String, val subtitle: String, val color: String, val unlocked: Boolean)
}
