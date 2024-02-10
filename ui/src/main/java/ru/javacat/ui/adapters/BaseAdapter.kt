package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.ui.R
import ru.javacat.ui.databinding.NameItemBinding

interface OnItemListener<T> {
    fun onItem(item: T)
}
abstract class BaseAdapter<T>(
    private val onItemListener: OnItemListener<T>
) : ListAdapter<T, BaseAdapter.Holder<T>>(Comparator<T>()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<T> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_item, parent, false)
        return Holder(view, onItemListener)
    }

    override fun onBindViewHolder(holder: Holder<T>, position: Int) {
        holder.bind(getItem(position))

    }

    class Holder<T>(view: View, private val onVehicleListener: OnItemListener<T>) :
        RecyclerView.ViewHolder(view) {
        private val binding = NameItemBinding.bind(view)
        fun bind(item: T) {
            //binding.name.text = item.regNumber
            binding.root.setOnClickListener {
                onVehicleListener.onItem(item)
            }
        }
    }

    class Comparator<T> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
            TODO("Not yet implemented")
        }

        override fun areContentsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
            TODO("Not yet implemented")
        }
    }
}