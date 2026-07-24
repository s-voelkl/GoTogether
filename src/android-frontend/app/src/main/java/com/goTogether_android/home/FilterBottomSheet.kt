package com.goTogether_android.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.goTogether_android.R
import com.goTogether_android.data.FILTER_CATEGORIES
import com.google.android.material.card.MaterialCardView

class FilterBottomSheet(
    private val onApply: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private val selectedCategories = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.bottomsheet_filter, container, false)

        val chipsContainer = v.findViewById<LinearLayout>(R.id.filterChipsContainer)
        setupChips(chipsContainer)

        v.findViewById<Button>(R.id.clearFiltersBtn).setOnClickListener {
            selectedCategories.clear()
            updateAllChips(chipsContainer)
        }

        v.findViewById<Button>(R.id.applyFiltersBtn).setOnClickListener {
            onApply(selectedCategories.toList())
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
            
            chipView.setOnClickListener {
                if (selectedCategories.contains(cat.id)) {
                    selectedCategories.remove(cat.id)
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
