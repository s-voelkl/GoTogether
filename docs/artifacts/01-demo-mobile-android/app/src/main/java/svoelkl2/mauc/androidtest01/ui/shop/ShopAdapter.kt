package svoelkl2.mauc.androidtest01.ui.shop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import svoelkl2.mauc.androidtest01.R
import svoelkl2.mauc.androidtest01.ShopItem

class ShopAdapter(
    private var items: List<ShopItem>,
    private val onBuyClick: (ShopItem) -> Unit
) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    class ShopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emoji: TextView = view.findViewById(R.id.itemEmoji)
        val name: TextView = view.findViewById(R.id.itemName)
        val price: TextView = view.findViewById(R.id.itemPrice)
        val purchaseCount: TextView = view.findViewById(R.id.itemPurchaseCount)
        val buyButton: Button = view.findViewById(R.id.buyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false)
        return ShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val item = items[position]
        holder.emoji.text = item.emoji
        holder.name.text = item.name
        holder.price.text = "${item.price} Gold"
        holder.purchaseCount.text = "Bought: ${item.purchaseCount}"
        holder.buyButton.setOnClickListener { onBuyClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<ShopItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}