package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Employee
import ru.javacat.ui.R
import ru.javacat.ui.databinding.EmployeeItemBinding

interface OnEmployeeListener {
    fun onEmployee(item: Employee)
}
class EmployeesAdapter(
    private val onEmployeeListener: OnEmployeeListener
): ListAdapter<Employee, EmployeesAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.employee_item, parent, false)
        return Holder(view, onEmployeeListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onEmployeeListener: OnEmployeeListener): RecyclerView.ViewHolder(view){
        private val binding = EmployeeItemBinding.bind(view)

        fun bind(item: Employee){
            binding.apply {
                name.text = item.name
                phoneNumber.text = item.phoneNumber
                email.text = item.email
                root.setOnClickListener {
                    onEmployeeListener.onEmployee(item)
                }
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Employee>(){
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }
}