package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.common.utils.asDayAndMonthShortly
import ru.javacat.common.utils.toPrettyPrice
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

        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.card_appearing)
        holder.itemView.startAnimation(animation)

    }

    class Holder(view: View, private val onItem: (Order) -> Unit): RecyclerView.ViewHolder(view){
        private val binding = OrderItemBinding.bind(view)

        fun bind(item: Order) {

            val pointsList = mutableListOf<String>()

            item.points.forEach {
                pointsList.add("${it.location} ${it.arrivalDate.asDayAndMonthShortly()}")
            }
            val pointsText: String = pointsList.joinToString (separator = " - ")

            if (item.isPaidByCustomer){
                binding.income.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_theme_primary))
            } else binding.income.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))

            val startDate = item.date.asDayAndMonthFully()

            val orderId = item.id

//            val orderWord = Resources.getSystem().getString(R.string.order)
//            val fromWord = Resources.getSystem().getString(R.string.from)
//            val routeWord = Resources.getSystem().getString(R.string.route)

            val orderIdString = "Заявка № $orderId от $startDate / Рейс № ${item.routeId} "
            val contractorString = if (item.contractor?.company?.id != -1L) "${item.contractor?.driver?.surname} (${item.contractor?.company?.shortName})" else item.contractor?.driver?.nameToShow
            val incomeString = item.price?.toPrettyPrice()+" р."

            binding.orderId.text = orderIdString
            binding.customerName.text = item.customer?.nameToShow
            binding.contractorName.text = contractorString
            binding.points.text = pointsText
            binding.income.text = incomeString
            if (item.isPaidByCustomer) binding.paymentDeadLineTv.isGone = true else {
                binding.paymentDeadLineTv.text = if (item.paymentDeadline == null){
                    "Срок оплаты ${item.daysToPay.toString()} дней"
                } else "Оплата до ${item.paymentDeadline!!.asDayAndMonthShortly()}"
            }


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