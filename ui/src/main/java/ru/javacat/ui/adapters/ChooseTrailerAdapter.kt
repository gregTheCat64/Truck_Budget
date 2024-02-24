package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.Trailer
import ru.javacat.ui.databinding.NameItemOnelineBinding

class ChooseTrailerAdapter(
    val onItem: (Trailer) -> Unit
): MyBaseAdapter<Trailer, NameItemOnelineBinding>({old, new -> old.id == new.id  }) {

    override val inflater: (LayoutInflater, ViewGroup) -> NameItemOnelineBinding = {li, vg->
        NameItemOnelineBinding.inflate(li,vg,false)
    }

    override fun bind(item: Trailer) {
        binding.itemName.text = item.regNumber
        binding.root.setOnClickListener {
            onItem(item)
        }
    }
}