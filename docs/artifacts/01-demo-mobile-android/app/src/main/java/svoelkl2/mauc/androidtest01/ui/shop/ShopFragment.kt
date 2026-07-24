package svoelkl2.mauc.androidtest01.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import svoelkl2.mauc.androidtest01.MainViewModel
import svoelkl2.mauc.androidtest01.R

class ShopFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_shop, container, false)
        
        val currencyText = root.findViewById<TextView>(R.id.currencyText)
        val recyclerView = root.findViewById<RecyclerView>(R.id.shopRecyclerView)
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ShopAdapter(emptyList()) { item ->
            if ((viewModel.currency.value ?: 0) >= item.price) {
                viewModel.buyItem(item)
                Toast.makeText(context, "Bought ${item.name}!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Not enough Gold!", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter

        viewModel.currency.observe(viewLifecycleOwner) { currency ->
            currencyText.text = "Currency: $currency Gold"
        }

        viewModel.shopItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        return root
    }
}