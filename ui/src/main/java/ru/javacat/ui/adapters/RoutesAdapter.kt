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
            it.customer?.shortName
        }

        binding.routeTitleTextView.text = "Рейс №${item.id} от ${item.startDate}"

        item.profit?.let {
            binding.earnedMoneyTextView.text = it.toString() + " р."
        }
//        item.contractor?.company?.shortName?.let {
//            binding.contractorNameTv.text = it
//        }
        val contractorString = "${item.contractor?.driver?.surname.toString()} (${item.contractor?.company?.shortName})"

        binding.truckDriverName.text = contractorString

        if (customersList.isNotEmpty()){
            binding.customersListTextView.text = customersList.toString()
        }

        binding.root.setOnClickListener {
            onRouteListener.onItem(item)
        }
    }
}