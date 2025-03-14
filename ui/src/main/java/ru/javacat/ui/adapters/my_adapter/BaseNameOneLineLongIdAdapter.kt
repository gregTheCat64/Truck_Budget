package ru.javacat.ui.adapters.my_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.BaseNameModel
import ru.javacat.ui.R
import ru.javacat.ui.databinding.NameItemBinding
import ru.javacat.ui.databinding.NameItemOnelineBinding

abstract class BaseNameOneLineLongIdAdapter<M: BaseNameModel<Long>>(
    open val onItem: (M) -> Unit
) : ListAdapter<M, BaseNameOneLineLongIdAdapter.Holder<M>>(BaseNameComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<M> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_item_oneline, parent, false)
        return Holder(view, onItem)
    }

    override fun onBindViewHolder(holder: Holder<M>, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder<M: BaseNameModel<Long>>(view: View, val onItem: (M) -> Unit):
        RecyclerView.ViewHolder(view) {
        private val binding = NameItemOnelineBinding.bind(view)
        fun bind(item: M) {
            binding.name.text = item.nameToShow
            binding.root.setOnClickListener {
                onItem(item)
            }
        }
    }

    class BaseNameComparator<M: BaseNameModel<Long>> : DiffUtil.ItemCallback<M>() {
        override fun areItemsTheSame(oldItem: M, newItem: M): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: M, newItem: M): Boolean {
            return oldItem.id == newItem.id
        }
    }
}