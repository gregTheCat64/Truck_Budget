package ru.javacat.ui.adapters.my_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.BaseNameModel
import ru.javacat.ui.R
import ru.javacat.ui.databinding.NameAndRemoveItemBinding

interface OnModelWithRemoveBtnListener{
    fun onItem(model: BaseNameModel<Long>)
    fun onRemove(model: BaseNameModel<Long>)
}

abstract class BaseNameAndRemoveLongIdAdapter<M: BaseNameModel<Long>>(
    private val onListener: OnModelWithRemoveBtnListener
) : ListAdapter<M, BaseNameAndRemoveLongIdAdapter.Holder<M>>(BaseNameComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<M> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_and_remove_item, parent, false)
        return Holder(view, onListener)
    }

    override fun onBindViewHolder(holder: Holder<M>, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder<M: BaseNameModel<Long>>(view: View, private  val onListener: OnModelWithRemoveBtnListener):
        RecyclerView.ViewHolder(view) {
        private val binding = NameAndRemoveItemBinding.bind(view)
        fun bind(item: M) {
            binding.name.text = item.nameToShow
            binding.removeBtn.setOnClickListener {
                onListener.onRemove(item)
            }
            binding.root.setOnClickListener {
                onListener.onItem(item)
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