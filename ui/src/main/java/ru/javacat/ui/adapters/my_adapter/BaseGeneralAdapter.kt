package ru.javacat.ui.adapters.my_adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

abstract class BaseGeneralAdapter<M : Any, VB: ViewBinding>(
    val onItem: (M) -> Unit
) : ListAdapter<M, BaseGeneralAdapter.Holder<M, VB>>(object : Comparator<M>(){
    override fun areItemsTheSame(oldItem: M, newItem: M): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: M, newItem: M): Boolean {
        return false
    }
}) {

    abstract class Holder<M : Any, VB : ViewBinding>(
        val onItem: (M) -> Unit,
        binding: VB
    ) : ViewHolder(binding.root) {
        abstract fun bind(item: M)
    }

    abstract class Comparator<M: Any>: DiffUtil.ItemCallback<M>()

}