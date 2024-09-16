package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Trailer
import ru.javacat.ui.R
import ru.javacat.ui.databinding.TrailersItemBinding
import ru.javacat.ui.databinding.TruckItemBinding

class TrailersAdapter(
    val onItem: (Trailer) -> Unit
): ListAdapter<Trailer, TrailersAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.truck_item, parent, false)
        return Holder(view, onItem)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))

        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.card_appearing)
        holder.itemView.startAnimation(animation)
    }

    class Holder(view: View, private val onItem: (Trailer) -> Unit): RecyclerView.ViewHolder(view){
        private val binding = TruckItemBinding.bind(view)

        fun bind(item: Trailer){
            binding.regNumberTv.text = item.nameToShow
            val trailerModel = item.model?:""
            val trailerYear = item.yearOfManufacturing?:""
            val trailerInfo = "$trailerModel $trailerYear"
            binding.infoTv.text = trailerInfo
            binding.root.setOnClickListener {
                onItem(item)
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Trailer>(){
        override fun areItemsTheSame(oldItem: Trailer, newItem: Trailer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Trailer, newItem: Trailer): Boolean {
            return oldItem == newItem
        }
    }
}