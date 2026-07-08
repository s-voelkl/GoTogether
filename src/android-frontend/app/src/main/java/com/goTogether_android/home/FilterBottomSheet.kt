package com.goTogether_android.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.goTogether_android.R
import com.goTogether_android.data.FILTER_CATEGORIES
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider

class FilterBottomSheet(
    private val initialSelectedCategories: List<String>,
    private val initialMinSocialBattery: Int,
    private val onApply: (List<String>, Int) -> Unit
) : BottomSheetDialogFragment() {

    private val selectedCategories = mutableListOf<String>()
    private var minSocialBattery = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.bottomsheet_filter, container, false)

        selectedCategories.addAll(initialSelectedCategories)
        minSocialBattery = initialMinSocialBattery

        val chipsContainer = v.findViewById<LinearLayout>(R.id.filterChipsContainer)
        setupChips(chipsContainer)

        val batterySlider = v.findViewById<Slider>(R.id.socialBatterySlider)
        val batteryValueText = v.findViewById<TextView>(R.id.socialBatteryValueText)
        
        batterySlider.value = minSocialBattery.toFloat()
        batteryValueText.text = "$minSocialBattery%"
        
        batterySlider.addOnChangeListener { _, value, _ ->
            minSocialBattery = value.toInt()
            batteryValueText.text = "$minSocialBattery%"
        }

        v.findViewById<Button>(R.id.setAllFiltersBtn).setOnClickListener {
            selectedCategories.clear()
            selectedCategories.addAll(FILTER_CATEGORIES.map { it.id })
            updateAllChips(chipsContainer)
        }

        v.findViewById<Button>(R.id.applyFiltersBtn).setOnClickListener {
            if (selectedCategories.isEmpty()) {
                Toast.makeText(requireContext(), "Select at least one category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onApply(selectedCategories.toList(), minSocialBattery)
            dismiss()
        }

        return v
    }

    private fun setupChips(container: LinearLayout) {
        val inflater = LayoutInflater.from(requireContext())
        FILTER_CATEGORIES.forEach { cat ->
            val chipView = inflater.inflate(R.layout.item_filter_chip, container, false)
            chipView.findViewById<TextView>(R.id.filterEmoji).text = cat.emoji
            chipView.findViewById<TextView>(R.id.filterLabel).text = cat.label
            
            updateChipUI(chipView, selectedCategories.contains(cat.id))
            
            chipView.setOnClickListener {
                if (selectedCategories.contains(cat.id)) {
                    if (selectedCategories.size > 1) {
                        selectedCategories.remove(cat.id)
                    } else {
                        Toast.makeText(requireContext(), "At least one category must be selected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedCategories.add(cat.id)
                }
                updateChipUI(chipView, selectedCategories.contains(cat.id))
            }
            
            container.addView(chipView)
        }
    }

    private fun updateAllChips(container: LinearLayout) {
        for (i in 0 until container.childCount) {
            val chipView = container.getChildAt(i)
            val cat = FILTER_CATEGORIES[i]
            updateChipUI(chipView, selectedCategories.contains(cat.id))
        }
    }

    private fun updateChipUI(view: View, isSelected: Boolean) {
        val card = view.findViewById<MaterialCardView>(R.id.filterChipCard)
        val checkbox = view.findViewById<MaterialCardView>(R.id.filterCheckbox)
        val checkMark = view.findViewById<ImageView>(R.id.filterCheckMark)
        
        if (isSelected) {
            card.setCardBackgroundColor(Color.parseColor("#FFB800")) // Primary color
            checkbox.setCardBackgroundColor(Color.BLACK)
            checkMark.visibility = View.VISIBLE
        } else {
            card.setCardBackgroundColor(Color.WHITE)
            checkbox.setCardBackgroundColor(Color.WHITE)
            checkMark.visibility = View.GONE
        }
    }
}
