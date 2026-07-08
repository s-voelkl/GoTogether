package com.goTogether_android.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.goTogether_android.R
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment that displays the user's profile and social battery settings.
 * Allows users to manually adjust their social battery level.
 */
class ProfileFragment : Fragment() {

    companion object {
        private const val PREFS_NAME = "profile_prefs"
        private const val KEY_SAVED_BATTERY = "saved_battery"
        private const val KEY_LAST_CHECK_AT = "last_check_at"
        private const val INITIAL_BATTERY = 60
    }

    private var savedBattery: Int = INITIAL_BATTERY
    private var lastCheckAt: Long = 0

    private lateinit var statusChip: TextView
    private lateinit var lastUpdateText: TextView
    private lateinit var batteryValueText: TextView
    private lateinit var batteryMessageText: TextView
    private lateinit var segmentContainer: LinearLayout
    private lateinit var batterySlider: Slider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)
        
        initViews(view)
        loadData()
        setupListeners()
        updateUI()
        
        return view
    }

    private fun initViews(view: View) {
        statusChip = view.findViewById(R.id.statusChip)
        lastUpdateText = view.findViewById(R.id.lastUpdateText)
        batteryValueText = view.findViewById(R.id.batteryValueText)
        batteryMessageText = view.findViewById(R.id.batteryMessageText)
        segmentContainer = view.findViewById(R.id.segmentContainer)
        batterySlider = view.findViewById(R.id.batterySlider)
    }

    private fun loadData() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        savedBattery = prefs.getInt(KEY_SAVED_BATTERY, INITIAL_BATTERY)
        lastCheckAt = prefs.getLong(KEY_LAST_CHECK_AT, 0L)
        
        batterySlider.value = savedBattery.toFloat()
    }

    private fun saveData() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()
        prefs.edit().apply {
            putInt(KEY_SAVED_BATTERY, savedBattery)
            putLong(KEY_LAST_CHECK_AT, currentTime)
            apply()
        }
        lastCheckAt = currentTime
        updateUI()
    }

    private fun setupListeners() {
        batterySlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                savedBattery = value.toInt()
                saveData()
            }
        }
        
        view?.findViewById<View>(R.id.appSettingsRow)?.setOnClickListener { openSettings() }
    }

    private fun updateUI() {
        val refreshNeeded = needsDailyRefresh(lastCheckAt)

        // Update status chip appearance based on refresh need
        if (refreshNeeded) {
            statusChip.text = getString(R.string.status_update_needed)
            statusChip.setBackgroundResource(R.drawable.bg_status_chip_alert)
        } else {
            statusChip.text = getString(R.string.status_checked_in)
            statusChip.setBackgroundResource(R.drawable.bg_status_chip_ready)
        }

        lastUpdateText.text = formatLastCheck(lastCheckAt)
        batteryValueText.text = "$savedBattery%"
        batteryMessageText.text = getBatteryMessage(savedBattery)

        updateSegments()
    }

    private fun updateSegments() {
        val filledSegments = Math.round(savedBattery / 20f)
        for (i in 0 until segmentContainer.childCount) {
            val segment = segmentContainer.getChildAt(i)
            if (i < filledSegments) {
                segment.setBackgroundResource(R.drawable.bg_segment_filled)
            } else {
                segment.setBackgroundResource(R.drawable.bg_segment_empty)
            }
        }
    }

    private fun needsDailyRefresh(lastCheck: Long): Boolean {
        if (lastCheck == 0L) return true
        val lastDate = Calendar.getInstance().apply { timeInMillis = lastCheck }
        val currentDate = Calendar.getInstance()
        
        return lastDate.get(Calendar.YEAR) != currentDate.get(Calendar.YEAR) ||
               lastDate.get(Calendar.DAY_OF_YEAR) != currentDate.get(Calendar.DAY_OF_YEAR)
    }

    private fun formatLastCheck(lastCheck: Long): String {
        if (lastCheck == 0L) return getString(R.string.no_daily_checkin)
        if (!needsDailyRefresh(lastCheck)) return getString(R.string.updated_today)
        
        val df = SimpleDateFormat("dd MMM", Locale.US)
        return "Last updated ${df.format(Date(lastCheck))}"
    }

    private fun getBatteryMessage(battery: Int): String {
        return when {
            battery >= 80 -> getString(R.string.battery_msg_high)
            battery >= 60 -> getString(R.string.battery_msg_mid_high)
            battery >= 40 -> getString(R.string.battery_msg_mid)
            battery >= 20 -> getString(R.string.battery_msg_mid_low)
            else -> getString(R.string.battery_msg_low)
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}
