package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.Truck
import ru.javacat.ui.databinding.NameItemOnelineBinding

class ChooseTruckAdapter(
    val onItem: (Truck) -> Unit
):MyBaseAdapter<Truck, NameItemOnelineBinding>({old, new -> old.id == new.id  }) {

    override val inflater: (LayoutInflater, ViewGroup) -> NameItemOnelineBinding = {li, vg->
        NameItemOnelineBinding.inflate(li, vg,false)
    }

    override fun bind(item: Truck) {
        binding.itemName.text = item.regNumber
        binding.root.setOnClickListener {
            onItem(item)
        }
    }
}