package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Point
import ru.javacat.ui.R
import ru.javacat.ui.databinding.PointItemBinding

interface OnPointListener {
    fun removePoint(item: Point)
}
class PointsAdapter(
    private val onPointListener: OnPointListener
): ListAdapter<Point, PointsAdapter.Holder>(Comparator()) {
        class Holder(view: View, private val onPointListener: OnPointListener): RecyclerView.ViewHolder(view){
        private val binding = PointItemBinding.bind(view)

        fun bind(item: Point){
            binding.apply {
                location.text = item.location.name
                date.text = item.arrivalDate.toString()
                removeBtn.setOnClickListener {
                onPointListener.removePoint(item)
                }
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Point>(){
        override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.point_item, parent, false)
        return Holder(view, onPointListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}

