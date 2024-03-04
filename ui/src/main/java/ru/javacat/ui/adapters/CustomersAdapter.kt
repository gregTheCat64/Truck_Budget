package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Customer
import ru.javacat.ui.R
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter
import ru.javacat.ui.databinding.NameItemBinding


class CustomersAdapter(override val onItem: (Customer) -> Unit): BaseNameLongIdAdapter<Customer>(onItem)