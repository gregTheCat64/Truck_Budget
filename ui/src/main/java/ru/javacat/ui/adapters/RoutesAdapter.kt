package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.my_adapter.MyBaseAdapter
import ru.javacat.ui.databinding.RouteItemBinding

    interface OnRouteListener {
        fun onItem(item: Route)
        fun onRemove(item: Route)
    }
class RoutesAdapter(
    private val onRouteListener: OnRouteListener
): MyBaseAdapter<Route, RouteItemBinding>({ old, new -> old.id == new.id  }, { old, new -> old == new  }) {

    override val inflater: (LayoutInflater, ViewGroup) -> RouteItemBinding = {li,vg ->
        RouteItemBinding.inflate(li, vg, false)
    }

    override fun bind(item: Route) {
        val customersList = item.orderList.map {
            it.customer.shortName + " "+ it.price + "р."
        }

        binding.routeTitleTextView.text = "Рейс №${item.id} от ${item.startDate}"
        binding.earnedMoneyTextView.text = item.income?.toString()
        binding.customersListTextView.text = customersList.toString()
        binding.removeBtn.setOnClickListener {
            onRouteListener.onRemove(item)
        }
        binding.root.setOnClickListener {
            onRouteListener.onItem(item)
        }
    }
}