package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.BaseNameLongIdModel
import ru.javacat.domain.models.Employee
import ru.javacat.domain.models.Location
import ru.javacat.ui.R
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter

import ru.javacat.ui.databinding.NameItemBinding

class LocationAdapter(
    override val onItem: (Location) -> Unit
): BaseNameLongIdAdapter<Location>(onItem)