package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.Staff
import ru.javacat.ui.databinding.NameItemOnelineBinding


class ChooseDriverAdapter(
    val onItem: (Staff) -> Unit
): MyBaseAdapter<Staff, NameItemOnelineBinding>({old, new -> old.id == new.id  }) {

    override val inflater: (LayoutInflater, ViewGroup) -> NameItemOnelineBinding = {li, vg ->
        NameItemOnelineBinding.inflate(li, vg, false)
    }

    override fun bind(item: Staff) {
        binding.itemName.text = item.fullName
        binding.root.setOnClickListener {
            onItem(item)
        }
    }
        }
