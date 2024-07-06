package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.common.utils.asDayAndMonthShortly
import ru.javacat.domain.models.Point
import ru.javacat.ui.R
import ru.javacat.ui.databinding.PointItemOnelineBinding
import ru.javacat.ui.databinding.PointItemWithRmvBtnBinding

class PointWithRemoveAdapter(
    val onItem: (Point) -> Unit
): ListAdapter<Point, PointWithRemoveAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.point_item_with_rmv_btn, parent, false)
        return Holder(view, onItem)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, val onItem: (Point) -> Unit): RecyclerView.ViewHolder(view){
        private val binding = PointItemWithRmvBtnBinding.bind(view)

        fun bind(item: Point){
            binding.pointDateTv.text = item.arrivalDate.asDayAndMonthShortly()
            binding.pointNameTv.text = item.location
            binding.removeBtn.setOnClickListener {
                onItem(item)
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
}