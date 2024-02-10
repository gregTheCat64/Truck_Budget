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

interface OnCargoListener{
    fun onCargo(item: Cargo)
}
class CargoAdapter(
    private val onCargoListener: OnCargoListener
): ListAdapter<Cargo, CargoAdapter.Holder>(Comparator()) {

    class Holder(view: View, private val onCargoListener: OnCargoListener): RecyclerView.ViewHolder(view){
        private val binding = NameItemBinding.bind(view)

        fun bind(item: Cargo){
            binding.name.text = item.name
            binding.root.setOnClickListener {
                onCargoListener.onCargo(item)
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Cargo>(){
        override fun areItemsTheSame(oldItem: Cargo, newItem: Cargo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cargo, newItem: Cargo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_item, parent, false)
        return Holder(view, onCargoListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

}