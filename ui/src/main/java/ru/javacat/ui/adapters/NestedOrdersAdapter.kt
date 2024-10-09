package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.javacat.common.utils.asDayAndMonthShortly
import ru.javacat.common.utils.toPrettyPrice
import ru.javacat.domain.models.Order
import ru.javacat.ui.R
import ru.javacat.ui.databinding.OrderMiniItemBinding


class NestedOrdersAdapter(
    private val onItemClick: (Order) -> Unit
): ListAdapter<Order, NestedOrdersAdapter.Holder>(Comparator()) {

    inner class Holder(val binding: OrderMiniItemBinding): ViewHolder(binding.root){
        fun bind(item: Order){
            val inflater = LayoutInflater.from(binding.root.context)
            val red = inflater.context.resources.getColor(R.color.red, null)
            binding.customerName.text = item.customer?.nameToShow
            val pointsList = mutableListOf<String>()
            var pointsText = ""
            item.points.forEach {
                pointsList.add("${it.location} ${it.arrivalDate.asDayAndMonthShortly()}")
            }
            pointsText = pointsList.joinToString (separator = " - ")
            if (!item.isPaidByCustomer) binding.income.setTextColor(red)
            binding.points.text = pointsText
            binding.income.text = item.price?.toPrettyPrice()

            binding.root.setOnClickListener {
                //onMiniOrdersListener.onOrder()
                //println("clicked! ${item.customer?.nameToShow}")
                onItemClick(item)

            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OrderMiniItemBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}