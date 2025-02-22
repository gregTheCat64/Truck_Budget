package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.common.utils.asDayAndMonthShortly
import ru.javacat.common.utils.toPrettyPrice
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.ui.R
import ru.javacat.ui.databinding.OrderMiniItemBinding
import ru.javacat.ui.databinding.RouteNestedOrdersItemBinding
import kotlin.math.roundToInt

interface OnRouteListener {
        fun onRoute(item: Route)
        fun newOrder(item: Route)
        fun onRemove(item: Route)
    }
class RoutesAdapter(
    private val onRouteListener: OnRouteListener, private val onOrderClick: (Order) -> Unit
): androidx.recyclerview.widget.ListAdapter<Route, RoutesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.route_nested_orders_item, parent, false)
        val inflater = LayoutInflater.from(parent.context)
        val binding = RouteNestedOrdersItemBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))


        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.card_appearing)
        holder.itemView.startAnimation(animation)
    }

    inner class Holder(val binding: RouteNestedOrdersItemBinding): RecyclerView.ViewHolder(binding.root){
            //для вложенного адаптера
        //private val binding = RouteNestedOrdersItemBinding.bind(view)
        //private val miniOrdersRecyclerView: RecyclerView = binding.ordersLayout
        //private val nestedOrdersAdapter: NestedOrdersAdapter = NestedOrdersAdapter(onOrderClick)

        fun bind(item: Route){
            binding.ordersLayout.removeAllViews()
            val orderViews = item.orderList.toListView(binding.ordersLayout, onOrderClick)
            orderViews.forEach {
                binding.ordersLayout.addView(it)
            }

                //для вложенного адаптера
            //miniOrdersRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            //miniOrdersRecyclerView.adapter = nestedOrdersAdapter
            //nestedOrdersAdapter.submitList(item.orderList)

            binding.routeTitleTextView.text = "Рейс №${item.id}"

//            item.profit?.let {
//                binding.earnedMoneyTextView.text = it.roundToInt().toPrettyPrice() + "доллАров!"
//            }
            binding.earnedMoneyTextView.text = if (item.profit != null) {
                item.profit!!.roundToInt().toPrettyPrice() + " р."
            } else ""

            val contractorString =  if (item.contractor?.company?.id != -1L) "${item.contractor?.driver?.surname.toString()} (${item.contractor?.company?.shortName})" else item.contractor?.driver?.nameToShow.toString()

            binding.truckDriverName.text = contractorString

            binding.addOrderBtn.isGone = item.isFinished

            binding.earnedMoneyTextView.isGone = item.orderList.isEmpty()

            binding.addOrderBtn.setOnClickListener {
                onRouteListener.newOrder(item)
            }
            binding.root.setOnClickListener {
                onRouteListener.onRoute(item)
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Route>(){
        override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
            return oldItem == newItem
        }
    }
}

fun List<Order>.toListView(parent: ViewGroup, onOrderClick: (Order) -> Unit): List<View> = this.map { order->
    val inflater = LayoutInflater.from(parent.context)
    //val green = inflater.context.resources.getColor(R.color.green, null)
    val binding = OrderMiniItemBinding.inflate(inflater, parent, false)
    val red = inflater.context.resources.getColor(R.color.red, null)
    binding.customerName.text = order.customer?.nameToShow
    val pointsList = mutableListOf<String>()
    order.points.forEach {
        pointsList.add("${it.location} ${it.arrivalDate.asDayAndMonthShortly()}")
    }
    val pointsText: String = pointsList.joinToString (separator = " - ")
    if (!order.isPaidByCustomer) binding.income.setTextColor(red)
    binding.points.text = pointsText
    binding.income.text = order.price?.toPrettyPrice()

    binding.root.setOnClickListener { _ -> onOrderClick(order)}

    binding.root
}

