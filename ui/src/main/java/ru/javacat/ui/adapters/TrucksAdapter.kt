package ru.javacat.ui.adapters

import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Truck
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.javacat.ui.R
import ru.javacat.ui.databinding.TruckItemBinding

class TrucksAdapter(
    val onItem:(Truck) -> Unit
): ListAdapter<Truck, TrucksAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.truck_item, parent, false)
        return Holder(view,onItem)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onItem: (Truck) -> Unit): RecyclerView.ViewHolder(view){
        private val binding = TruckItemBinding.bind(view)

        fun bind(item: Truck){
            binding.regNumberTv.text = item.nameToShow
            binding.infoTv.text = "${item.model} ${item.yearOfManufacturing}"
            binding.root.setOnClickListener {
                onItem(item)
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Truck>(){
        override fun areItemsTheSame(oldItem: Truck, newItem: Truck): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Truck, newItem: Truck): Boolean {
            return oldItem == newItem
        }
    }
}