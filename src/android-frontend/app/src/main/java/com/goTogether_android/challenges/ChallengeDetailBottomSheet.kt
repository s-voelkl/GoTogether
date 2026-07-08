package com.goTogether_android.challenges

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.goTogether_android.R
import com.goTogether_android.data.*
import com.goTogether_android.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class ChallengeDetailBottomSheet : BottomSheetDialogFragment() {

    private var challengeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        challengeId = arguments?.getString(ARG_CHALLENGE_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.bottomsheet_challenge_detail, container, false)
        
        val challenge = ChallengeRepository.findById(challengeId ?: "") ?: return v
        
        bindData(v, challenge)
        
        v.findViewById<Button>(R.id.showOnMapButton).setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("focusChallengeId", challenge.id)
            }
            startActivity(intent)
            dismiss()
        }
        
        return v
    }

    private fun bindData(v: View, challenge: Challenge) {
        val catInfo = FILTER_CATEGORIES.find { it.id == challenge.category }
        
        v.findViewById<TextView>(R.id.detailEmoji).text = catInfo?.emoji ?: "📍"
        v.findViewById<TextView>(R.id.detailName).text = challenge.name
        v.findViewById<TextView>(R.id.detailHost).text = "Hosted by ${challenge.host}"
        v.findViewById<TextView>(R.id.detailPoints).text = "${challenge.points} pts"
        v.findViewById<TextView>(R.id.detailDescription).text = challenge.description

        // Icon Box background
        val iconBox = v.findViewById<View>(R.id.detailIconBox)
        val accentColor = Color.parseColor(catInfo?.color ?: "#6B7280")
        val alphaColor = Color.argb(40, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor))
        val bg = GradientDrawable().apply {
            cornerRadius = resources.getDimension(R.dimen.nearby_radius)
            setColor(alphaColor)
        }
        iconBox.background = bg

        // Difficulty Badge
        val diffBadge = v.findViewById<TextView>(R.id.detailDiffBadge)
        val difficulty = challenge.getDifficulty()
        val diffColor = Color.parseColor(DIFFICULTY_COLORS[difficulty] ?: "#6B7280")
        diffBadge.text = difficulty.name.lowercase()
        diffBadge.setTextColor(diffColor)
        val diffBg = GradientDrawable().apply {
            cornerRadius = resources.displayMetrics.density * 100
            setColor(Color.argb(34, Color.red(diffColor), Color.green(diffColor), Color.blue(diffColor)))
        }
        diffBadge.background = diffBg

        // Full Badge
        val isFull = challenge.isFull()
        v.findViewById<View>(R.id.detailFullBadge).visibility = if (isFull) View.VISIBLE else View.GONE

        // Info Grid
        v.findViewById<TextView>(R.id.infoWhen).text = formatWhen(challenge.startTime)
        v.findViewById<TextView>(R.id.infoDuration).text = formatDuration(challenge.durationMinutes)
        v.findViewById<TextView>(R.id.infoPlayers).text = formatPlayers(challenge)
        v.findViewById<TextView>(R.id.infoSocialBattery).text = "${challenge.minSocialBattery}/5"
    }

    private fun formatWhen(iso: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val date = inputFormat.parse(iso)
            val outputFormat = SimpleDateFormat("EEE dd MMM · HH:mm", Locale.US)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            iso
        }
    }

    private fun formatDuration(m: Int): String {
        return if (m >= 60) {
            val hours = m / 60
            val mins = m % 60
            if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
        } else {
            "${m} min"
        }
    }

    private fun formatPlayers(c: Challenge): String {
        return if (c.maxPlayers == 0) {
            "${c.participants} joined · unlimited"
        } else {
            "${c.participants}/${c.maxPlayers} joined"
        }
    }

    companion object {
        private const val ARG_CHALLENGE_ID = "challenge_id"
        
        fun newInstance(id: String): ChallengeDetailBottomSheet {
            return ChallengeDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHALLENGE_ID, id)
                }
            }
        }
    }
}
