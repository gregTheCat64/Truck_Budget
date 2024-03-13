package ru.javacat.ui.adapters.my_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import ru.javacat.domain.models.BaseNameModel

abstract class BaseGeneralAdapter<M : BaseNameModel<Long>, VB: ViewBinding>(
    val onItem: (M) -> Unit
) : ListAdapter<M, BaseGeneralAdapter.Holder<M, VB>>(Comparator<M>()){

    abstract class Holder<M : Any, VB : ViewBinding>(
        val onItem: (M) -> Unit,
        binding: VB
    ) : ViewHolder(binding.root) {
        abstract fun bind(item: M)
    }

    open class Comparator<M: BaseNameModel<Long>>: DiffUtil.ItemCallback<M>() {
        override fun areItemsTheSame(oldItem: M, newItem: M): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: M, newItem: M): Boolean {
            return oldItem.id == newItem.id
        }
    }

}