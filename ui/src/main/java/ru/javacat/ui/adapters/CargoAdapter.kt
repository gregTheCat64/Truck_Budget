package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Point
import ru.javacat.ui.R
import ru.javacat.ui.databinding.NameItemBinding


class CargoAdapter(
    val onItem: (Cargo) -> Unit
):MyBaseAdapter<Cargo, NameItemBinding>({old, new -> old.id == new.id }) {

    override val inflater: (LayoutInflater, ViewGroup) -> NameItemBinding = {li, vg->
        NameItemBinding.inflate(li,vg, false)
    }

    override fun bind(item: Cargo) {
        binding.name.text = item.name
        binding.root.setOnClickListener {
            onItem(item)
        }
    }
}