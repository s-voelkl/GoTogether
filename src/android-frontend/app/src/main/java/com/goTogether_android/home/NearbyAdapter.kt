package com.goTogether_android.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goTogether_android.R
import com.goTogether_android.data.Challenge
import com.goTogether_android.util.setupChallengeCard

class NearbyAdapter(
    private val onPress: (Challenge) -> Unit
) : RecyclerView.Adapter<NearbyAdapter.ViewHolder>() {

    val items = mutableListOf<Challenge>()

    fun submitList(newItems: List<Challenge>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun scrollTo(position: Int) {
        // Handled by RecyclerView.scrollToPosition in Fragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_challenge_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val challenge = items[position]
        setupChallengeCard(holder.itemView, challenge)
        holder.itemView.setOnClickListener { onPress(challenge) }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
