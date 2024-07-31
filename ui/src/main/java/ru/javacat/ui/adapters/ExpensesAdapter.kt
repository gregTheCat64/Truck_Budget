package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.domain.models.Expense
import ru.javacat.domain.models.Order
import ru.javacat.ui.R
import ru.javacat.ui.databinding.ExpenseItemBinding

class ExpensesAdapter(
    val onItem: (Expense) -> Unit
): ListAdapter<Expense, ExpensesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        return Holder(view, onItem)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onItem: (Expense) -> Unit): RecyclerView.ViewHolder(view){
        private val binding = ExpenseItemBinding.bind(view)

        fun bind(item: Expense) {
            binding.apply {
                expenseName.text = item.name
                expenseDescription.text = item.description
                expenseCost.text = item.price.toString()
                expenseDate.text = item.date.asDayAndMonthFully()
            }

            binding.root.setOnClickListener {
                onItem(item)
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Expense>(){
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}