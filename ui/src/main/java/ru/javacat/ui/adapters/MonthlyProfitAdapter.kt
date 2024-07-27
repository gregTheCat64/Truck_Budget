package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.MonthlyProfit
import ru.javacat.ui.R
import ru.javacat.ui.databinding.MonthlyProfitItemBinding

class MonthlyProfitAdapter: ListAdapter<MonthlyProfit, MonthlyProfitAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.monthly_profit_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View):RecyclerView.ViewHolder(view){
        private val binding = MonthlyProfitItemBinding.bind(view)

        fun bind(item: MonthlyProfit){
            binding.month.text = item.month.toString()
            binding.profitTv.text = item.totalProfit.toString()
        }
    }

    class Comparator: DiffUtil.ItemCallback<MonthlyProfit>(){
        override fun areItemsTheSame(oldItem: MonthlyProfit, newItem: MonthlyProfit): Boolean {
            return oldItem.month != newItem.month
        }

        override fun areContentsTheSame(oldItem: MonthlyProfit, newItem: MonthlyProfit): Boolean {
            return oldItem == newItem
        }
    }
}