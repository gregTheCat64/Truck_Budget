package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.Customer
import ru.javacat.ui.databinding.NameItemOnelineBinding

class ChooseCustomerAdapter(
    val onItem: (Customer) -> Unit
): MyBaseAdapter<Customer, NameItemOnelineBinding>({old, new -> old.id == new.id  }) {
    override val inflater: (LayoutInflater, ViewGroup) -> NameItemOnelineBinding
        get() = {li,vg->
            NameItemOnelineBinding.inflate(li, vg, false)
        }

    override fun bind(item: Customer) {
        binding.itemName.text = item.name
        binding.root.setOnClickListener {
            onItem(item)
        }
    }
}