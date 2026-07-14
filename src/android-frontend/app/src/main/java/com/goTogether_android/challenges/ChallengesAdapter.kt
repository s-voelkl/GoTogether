package com.goTogether_android.challenges

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goTogether_android.R
import com.goTogether_android.data.Challenge
import com.goTogether_android.util.setupChallengeCard

class ChallengesAdapter(
    private val onClick: (Challenge) -> Unit
) : ListAdapter<Challenge, ChallengesAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_challenge_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val challenge = getItem(position)
        setupChallengeCard(holder.itemView, challenge)
        holder.itemView.setOnClickListener { onClick(challenge) }
    }

    object DiffCallback : DiffUtil.ItemCallback<Challenge>() {
        override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean = oldItem == newItem
    }
}
