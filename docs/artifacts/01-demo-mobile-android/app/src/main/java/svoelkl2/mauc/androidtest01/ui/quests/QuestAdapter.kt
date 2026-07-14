package svoelkl2.mauc.androidtest01.ui.quests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import svoelkl2.mauc.androidtest01.R

class QuestAdapter(
    private var quests: List<Quest>,
    private val onQuestClick: (Quest) -> Unit
) : RecyclerView.Adapter<QuestAdapter.QuestViewHolder>() {

    class QuestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.questTitle)
        val type: TextView = view.findViewById(R.id.questType)
        val reward: TextView = view.findViewById(R.id.questReward)
        val status: TextView = view.findViewById(R.id.questStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quest, parent, false)
        return QuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val quest = quests[position]
        holder.title.text = quest.title
        holder.type.text = quest.type
        holder.reward.text = quest.reward
        
        if (quest.isDone) {
            holder.status.visibility = View.VISIBLE
            holder.status.text = "COMPLETED"
            holder.itemView.alpha = 0.6f
        } else {
            holder.status.visibility = View.GONE
            holder.itemView.alpha = 1.0f
        }

        holder.itemView.setOnClickListener { onQuestClick(quest) }
    }

    override fun getItemCount() = quests.size

    fun updateQuests(newQuests: List<Quest>) {
        quests = newQuests
        notifyDataSetChanged()
    }
}