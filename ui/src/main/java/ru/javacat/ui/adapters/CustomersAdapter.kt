package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Partner
import ru.javacat.ui.R
import ru.javacat.ui.databinding.CustomerItemBinding

interface OnCustomerListener {
    fun onCustomer(item: Partner)
}
class CustomersAdapter(
    private val onCustomerListener: OnCustomerListener
): ListAdapter<Partner, CustomersAdapter.Holder>(Comparator()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.customer_item, parent, false)
        return Holder(view, onCustomerListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onCustomerListener: OnCustomerListener): RecyclerView.ViewHolder(view){
        private val binding = CustomerItemBinding.bind(view)

        fun bind(item: Partner){
            binding.apply {
                companyName.text = item.nameToShow
                item.companyPhone?.let {
                    phoneNumber.text = it
                }
                item.atiNumber?.let {
                    atiNumber.text = it.toString()
                }
                root.setOnClickListener {
                    onCustomerListener.onCustomer(item)
                }
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Partner>(){
        override fun areItemsTheSame(oldItem: Partner, newItem: Partner): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Partner, newItem: Partner): Boolean {
            return oldItem == newItem
        }
    }
}