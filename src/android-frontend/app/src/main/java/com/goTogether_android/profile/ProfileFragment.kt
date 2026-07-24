package com.goTogether_android.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.goTogether_android.R
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private val PREFS_NAME = "profile_prefs"
    private val KEY_SAVED_BATTERY = "saved_battery"
    private val KEY_LAST_CHECK_AT = "last_check_at"
    private val INITIAL_BATTERY = 60
    private val QUICK_VALUES = listOf(20, 40, 60, 80, 100)

    private var savedBattery: Int = INITIAL_BATTERY
    private var draftBattery: Int = INITIAL_BATTERY
    private var lastCheckAt: Long = 0

    private lateinit var statusChip: TextView
    private lateinit var lastUpdateText: TextView
    private lateinit var batteryValueText: TextView
    private lateinit var batteryMessageText: TextView
    private lateinit var segmentContainer: LinearLayout
    private lateinit var savedValueText: TextView
    private lateinit var refreshStatusText: TextView
    private lateinit var inputSourceText: TextView
    private lateinit var saveButton: Button
    private lateinit var quickGrid: GridLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)
        
        initViews(view)
        loadData()
        setupListeners(view)
        updateUI()
        
        return view
    }

    private fun initViews(view: View) {
        statusChip = view.findViewById(R.id.statusChip)
        lastUpdateText = view.findViewById(R.id.lastUpdateText)
        batteryValueText = view.findViewById(R.id.batteryValueText)
        batteryMessageText = view.findViewById(R.id.batteryMessageText)
        segmentContainer = view.findViewById(R.id.segmentContainer)
        savedValueText = view.findViewById(R.id.savedValueText)
        refreshStatusText = view.findViewById(R.id.refreshStatusText)
        inputSourceText = view.findViewById(R.id.inputSourceText)
        saveButton = view.findViewById(R.id.saveButton)
        quickGrid = view.findViewById(R.id.quickGrid)

        setupQuickSelectGrid()
    }

    private fun loadData() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        savedBattery = prefs.getInt(KEY_SAVED_BATTERY, INITIAL_BATTERY)
        lastCheckAt = prefs.getLong(KEY_LAST_CHECK_AT, 0)
        draftBattery = savedBattery
    }

    private fun saveData() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt(KEY_SAVED_BATTERY, draftBattery)
            putLong(KEY_LAST_CHECK_AT, System.currentTimeMillis())
            apply()
        }
        loadData()
        updateUI()
    }

    private fun setupListeners(view: View) {
        view.findViewById<Button>(R.id.decrementButton).setOnClickListener { adjustBattery(-10) }
        view.findViewById<Button>(R.id.incrementButton).setOnClickListener { adjustBattery(10) }
        view.findViewById<View>(R.id.appSettingsRow).setOnClickListener { openSettings() }
        saveButton.setOnClickListener { saveData() }
    }

    private fun setupQuickSelectGrid() {
        val inflater = LayoutInflater.from(requireContext())
        quickGrid.removeAllViews()
        QUICK_VALUES.forEach { value ->
            val chipView = inflater.inflate(R.layout.item_quick_chip, quickGrid, false)
            val valueText = chipView.findViewById<TextView>(R.id.quickChipValue)
            val labelText = chipView.findViewById<TextView>(R.id.quickChipLabel)
            
            valueText.text = "$value%"
            labelText.text = getBatteryLabel(value)
            
            chipView.setOnClickListener {
                draftBattery = value
                updateUI()
            }
            
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(8, 8, 8, 8)
            chipView.layoutParams = params
            
            quickGrid.addView(chipView)
        }
    }

    private fun adjustBattery(delta: Int) {
        draftBattery = (draftBattery + delta).coerceIn(0, 100)
        updateUI()
    }

    private fun updateUI() {
        val refreshNeeded = needsDailyRefresh(lastCheckAt)
        val hasUnsavedChanges = draftBattery != savedBattery

        // Status Chip
        if (refreshNeeded) {
            statusChip.text = "Daily update needed"
            statusChip.setBackgroundResource(R.drawable.bg_status_chip_alert)
        } else {
            statusChip.text = "Checked in"
            statusChip.setBackgroundResource(R.drawable.bg_status_chip_ready)
        }

        // Hero Info
        lastUpdateText.text = formatLastCheck(lastCheckAt)
        batteryValueText.text = "$draftBattery%"
        batteryMessageText.text = getBatteryMessage(draftBattery)

        // Segments
        updateSegments()

        // Quick Select Selection
        updateQuickSelectSelection()

        // Profile Use
        savedValueText.text = "$savedBattery%"
        refreshStatusText.text = if (refreshNeeded) "Needed" else "Done"
        inputSourceText.text = if (hasUnsavedChanges) "Draft changed" else "Current profile"

        // Save Button
        saveButton.isEnabled = refreshNeeded || hasUnsavedChanges
        saveButton.alpha = if (saveButton.isEnabled) 1.0f else 0.35f
        saveButton.text = when {
            refreshNeeded -> "Save today's battery"
            hasUnsavedChanges -> "Update battery"
            else -> "Battery checked today"
        }
    }

    private fun updateSegments() {
        val filledSegments = Math.round(draftBattery / 20f)
        for (i in 0 until segmentContainer.childCount) {
            val segment = segmentContainer.getChildAt(i)
            if (i < filledSegments) {
                segment.setBackgroundResource(R.drawable.bg_segment_filled)
            } else {
                segment.setBackgroundResource(R.drawable.bg_segment_empty)
            }
        }
    }

    private fun updateQuickSelectSelection() {
        for (i in 0 until quickGrid.childCount) {
            val child = quickGrid.getChildAt(i)
            val value = QUICK_VALUES[i]
            val isSelected = value == draftBattery
            
            child.setBackgroundResource(if (isSelected) R.drawable.bg_quick_chip_selected else R.drawable.bg_quick_chip)
            
            val valueText = child.findViewById<TextView>(R.id.quickChipValue)
            val labelText = child.findViewById<TextView>(R.id.quickChipLabel)
            
            val color = ContextCompat.getColor(requireContext(), R.color.black)
            valueText.setTextColor(color)
            labelText.setTextColor(if (isSelected) ContextCompat.getColor(requireContext(), R.color.black) else ContextCompat.getColor(requireContext(), R.color.gray500))
            labelText.alpha = if (isSelected) 0.75f else 1.0f
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
        if (lastCheck == 0L) return "No daily check-in yet"
        if (!needsDailyRefresh(lastCheck)) return "Updated today"
        
        val df = SimpleDateFormat("dd MMM", Locale.UK)
        return "Last updated ${df.format(Date(lastCheck))}"
    }

    private fun getBatteryMessage(battery: Int): String {
        return when {
            battery >= 80 -> "Perfect for big social plans and busy group activities."
            battery >= 60 -> "A solid day for balanced plans and casual meetups."
            battery >= 40 -> "Better for lighter challenges and smaller groups."
            battery >= 20 -> "Good moment for low-pressure plans and short activities."
            else -> "Best to keep recommendations calm, short and easy today."
        }
    }

    private fun getBatteryLabel(battery: Int): String {
        return when {
            battery <= 20 -> "Low-key"
            battery <= 40 -> "Light"
            battery <= 60 -> "Balanced"
            battery <= 80 -> "Open"
            else -> "Full send"
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}
