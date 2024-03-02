package ru.javacat.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

interface OnItemListener<M> {
    fun onItem(item: M)
}

class MyComparator<M : Any>(
    val itemsTheSame: (old: M, new: M) -> Boolean
) : DiffUtil.ItemCallback<M>() {
    override fun areItemsTheSame(oldItem: M, newItem: M): Boolean {
        return itemsTheSame(oldItem, newItem)
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: M, newItem: M): Boolean {
        return oldItem == newItem
    }
}

abstract class VH<M : Any, VB : ViewBinding>(
    binding: VB
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: M)
}

abstract class MyBaseAdapter<M : Any, VB: ViewBinding>(
    areItemsTheSame:(old:M, new:M)->Boolean
) : ListAdapter<M, VH<M, VB>>(MyComparator<M>(areItemsTheSame)) {

    private var _binding: VB? = null

    protected abstract val inflater: (LayoutInflater, ViewGroup) -> VB

    protected val binding
        get() = _binding
            ?: throw NullPointerException("${this::class.simpleName} view binding failed")

    abstract fun bind(item: M)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<M, VB> {
        val li = LayoutInflater.from(parent.context)

        _binding = inflater(li, parent)

        return object : VH<M, VB>(binding){
            override fun bind(item: M) {
                this@MyBaseAdapter.bind(item)
            }
        }
    }

    override fun onBindViewHolder(holder: VH<M, VB>, position: Int) {
        holder.bind(getItem(position))
    }

}