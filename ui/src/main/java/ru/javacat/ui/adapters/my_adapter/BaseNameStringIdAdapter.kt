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

//Универсальный адаптер для одного слова-карточки с одним действием - кликом на нём. ID- String
abstract class BaseNameStringIdAdapter<M: BaseNameModel<String>>(
    val onItem: (M) -> Unit
) : ListAdapter<M, BaseNameStringIdAdapter.Holder<M>>(BaseNameStringIdComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<M> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_item, parent, false)
        return Holder(view, onItem)
    }

    override fun onBindViewHolder(holder: Holder<M>, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder<M: BaseNameModel<String>>(view: View, val onItem: (M) -> Unit):
        RecyclerView.ViewHolder(view) {
        private val binding = NameItemBinding.bind(view)
        fun bind(item: M) {
            binding.name.text = item.name
            binding.root.setOnClickListener {
                onItem(item)
            }
        }
    }

    class BaseNameStringIdComparator<M: BaseNameModel<String>> : DiffUtil.ItemCallback<M>() {
        override fun areItemsTheSame(oldItem: M, newItem: M): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: M, newItem: M): Boolean {
            return oldItem.id == newItem.id
        }
    }
}