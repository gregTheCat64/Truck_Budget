package ru.javacat.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.domain.models.Order
import ru.javacat.ui.MainActivity
import ru.javacat.ui.R
import ru.javacat.ui.adapters.my_adapter.MyBaseAdapter
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

            var startDate: String = ""

            if (item.isPaid){
                binding.income.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey))
            } else binding.income.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))

            if (points.isNotEmpty()){
                startDate = item.points[0].arrivalDate.asDayAndMonthFully()

            }
            val orderId = item.id
            //val orderWord = Resources.getSystem().getString(R.string.order)
            //val fromWord = Resources.getSystem().getString(R.string.from)

            val orderIdString = "Заявка № $orderId / рейс № ${item.routeId} от $startDate"

            binding.orderId.text = orderIdString
            binding.customerName.text = item.customer?.name
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