package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Employee
import ru.javacat.domain.models.Location
import ru.javacat.ui.R
import ru.javacat.ui.databinding.LocationItemBinding

interface OnLocationListener{
    fun onLocation(item: Location)
}
class LocationAdapter(
    private val onLocationListener: OnLocationListener
): androidx.recyclerview.widget.ListAdapter<Location, LocationAdapter.Holder>(Comparator()) {


    class Holder(view: View, private val onLocationListener: OnLocationListener): RecyclerView.ViewHolder(view){
        private val binding = LocationItemBinding.bind(view)

        fun bind(item: Location){
            binding.locationName.text = item.name
            binding.root.setOnClickListener {
                onLocationListener.onLocation(item)
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Location>(){
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        return Holder(view, onLocationListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}