package ru.javacat.ui.expense

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Expense
import ru.javacat.ui.R
import ru.javacat.ui.adapters.my_adapter.BaseNameOneLineLongIdAdapter
import ru.javacat.ui.databinding.ExpenseItemBinding
import ru.javacat.ui.databinding.NameAndRemoveItemBinding
import ru.javacat.ui.databinding.NameItemBinding
import ru.javacat.ui.databinding.NameItemOnelineBinding

class ExpenseNameAdapter(
    val onItem: (String) -> Unit
): ListAdapter<String, ExpenseNameAdapter.Holder>(Comparator()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_item, parent, false)
        return Holder(view, onItem)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onItem: (String) -> Unit):RecyclerView.ViewHolder(view){
        private val binding = NameItemBinding.bind(view)

        fun bind(item: String){
            binding.name.text = item
            binding.root.setOnClickListener {
                onItem(item)
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }



}