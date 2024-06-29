package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.domain.models.Order
import ru.javacat.ui.R
import ru.javacat.ui.databinding.OrderItemBinding

class OrdersAdapter(
    val onItem: (Order) -> Unit
): ListAdapter<Order, OrdersAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return Holder(view, onItem)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onItem: (Order) -> Unit): RecyclerView.ViewHolder(view){
        private val binding = OrderItemBinding.bind(view)

        fun bind(item: Order) {
            val points = mutableListOf<String>()
            for (i in item.points){
                points.add(i.location)
            }

            if (item.isPaidByCustomer){
                binding.income.setTextColor(ContextCompat.getColor(binding.root.context, R.color.md_theme_primary))
            } else binding.income.setTextColor(ContextCompat.getColor(binding.root.context, R.color.md_theme_error))

            val startDate = item.date.asDayAndMonthFully()

            val orderId = item.id

//            val orderWord = Resources.getSystem().getString(R.string.order)
//            val fromWord = Resources.getSystem().getString(R.string.from)
//            val routeWord = Resources.getSystem().getString(R.string.route)

            val orderIdString = "Заявка № $orderId от $startDate / Рейс № ${item.routeId} "

            binding.orderId.text = orderIdString
            binding.customerName.text = item.customer?.nameToShow
            binding.points.text = points.toString()
            binding.income.text = item.price.toString()+"р."

            binding.root.setOnClickListener {
                onItem(item)
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
}