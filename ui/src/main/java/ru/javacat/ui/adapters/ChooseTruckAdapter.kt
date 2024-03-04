package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.MyBaseAdapter
import ru.javacat.ui.databinding.NameItemOnelineBinding

class ChooseTruckAdapter(override val onItem: (Truck) -> Unit): BaseNameLongIdAdapter<Truck>(onItem)
