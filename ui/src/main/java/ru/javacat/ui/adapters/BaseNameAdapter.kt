package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.BaseNameModel
import ru.javacat.ui.R
import ru.javacat.ui.databinding.NameItemBinding

interface OnNameItemListener {
    fun onItem(item: BaseNameModel)
}
abstract class BaseNameAdapter(
    private val onItemListener: OnNameItemListener
) : ListAdapter<BaseNameModel, BaseNameAdapter.Holder>(Comparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_item, parent, false)
        return Holder(view, onItemListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onItemListener: OnNameItemListener):
        RecyclerView.ViewHolder(view) {
        private val binding = NameItemBinding.bind(view)
        fun bind(item: BaseNameModel) {
            binding.name.text = item.name
            binding.root.setOnClickListener {
                onItemListener.onItem(item)
            }
        }
    }

     class Comparator : DiffUtil.ItemCallback<BaseNameModel>() {
                override fun areItemsTheSame(oldItem: BaseNameModel, newItem: BaseNameModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BaseNameModel, newItem: BaseNameModel): Boolean {
            return oldItem.id == newItem.id
        }
    }
}