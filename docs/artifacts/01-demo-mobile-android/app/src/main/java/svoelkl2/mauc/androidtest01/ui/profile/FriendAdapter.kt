package svoelkl2.mauc.androidtest01.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import svoelkl2.mauc.androidtest01.Friend
import svoelkl2.mauc.androidtest01.R

class FriendAdapter(private val onClick: (Friend) -> Unit) : ListAdapter<Friend, FriendAdapter.FriendViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FriendViewHolder(itemView: View, val onClick: (Friend) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val nameText = itemView.findViewById<TextView>(R.id.friendName)
        private val levelText = itemView.findViewById<TextView>(R.id.friendLevel)
        private val scoreText = itemView.findViewById<TextView>(R.id.friendScore)

        fun bind(friend: Friend) {
            nameText.text = friend.name
            levelText.text = "Level ${friend.level}"
            scoreText.text = "Score: ${friend.virtualScore}"
            itemView.setOnClickListener { onClick(friend) }
        }
    }

    class FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean = oldItem == newItem
    }
}