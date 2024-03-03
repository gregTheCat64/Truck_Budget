package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.Order
import ru.javacat.ui.adapters.my_adapter.MyBaseAdapter
import ru.javacat.ui.databinding.OrderItemBinding

class OrdersAdapter(): MyBaseAdapter<Order, OrderItemBinding>({ old, new -> old.id == new.id  }, { old, new -> old == new  }) {
    override val inflater: (LayoutInflater, ViewGroup) -> OrderItemBinding
        get() = { layoutInflater, viewGroup ->
            OrderItemBinding.inflate(layoutInflater, viewGroup, false)
        }

    override fun bind(item: Order) {
        val points = mutableListOf<String>()
            for (i in item.points){
            points.add(i.location.name)
        }
        binding.customerName.text = item.customer?.name
        binding.points.text = points.toString()
        binding.income.text = item.price.toString()
        if (points.isNotEmpty()){
            binding.startDate.text = item.points[0].arrivalDate.toString()
        }

    }
}