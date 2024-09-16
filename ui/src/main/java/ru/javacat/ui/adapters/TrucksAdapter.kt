package ru.javacat.ui.adapters

import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Truck
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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

        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.card_appearing)
        holder.itemView.startAnimation(animation)
    }

    class Holder(view: View, private val onItem: (Truck) -> Unit): RecyclerView.ViewHolder(view){
        private val binding = TruckItemBinding.bind(view)

        fun bind(item: Truck){
            binding.regNumberTv.text = item.nameToShow
            val truckModel = item.model?:""
            val truckYear = item.yearOfManufacturing?:""
            val truckInfo = "$truckModel $truckYear"

            binding.infoTv.text = truckInfo
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