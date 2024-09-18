package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.common.utils.asDayAndMonthShortly
import ru.javacat.domain.models.Route
import ru.javacat.ui.R
import ru.javacat.ui.databinding.RouteItemBinding
import kotlin.math.roundToInt

interface OnRouteListener {
        fun onItem(item: Route)
        fun onRemove(item: Route)
    }
class RoutesAdapter(
    private val onItemListener: OnRouteListener
): androidx.recyclerview.widget.ListAdapter<Route, RoutesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.route_item, parent, false)
        return Holder(view, onItemListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))

        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.card_appearing)
        holder.itemView.startAnimation(animation)
    }

    class Holder(view: View, private val onItemListener: OnRouteListener): RecyclerView.ViewHolder(view){
        private val binding = RouteItemBinding.bind(view)

        fun bind(item: Route){
            val customersList = item.orderList.map {
                it.customer?.shortName
            }

            binding.routeTitleTextView.text = "Рейс №${item.id} от ${item.startDate?.asDayAndMonthShortly()}"

            item.profit?.let {
                binding.earnedMoneyTextView.text = it.roundToInt().toString() + " р."
            }
            val contractorString = "${item.contractor?.driver?.surname.toString()} (${item.contractor?.company?.shortName})"

            binding.truckDriverName.text = contractorString

            if (customersList.isNotEmpty()){
                binding.customersListTextView.text = customersList.toString()
            }

            binding.root.setOnClickListener {
                onItemListener.onItem(item)
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