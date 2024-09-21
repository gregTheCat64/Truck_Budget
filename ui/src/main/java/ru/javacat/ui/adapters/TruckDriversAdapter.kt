package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.TruckDriver
import ru.javacat.ui.R
import ru.javacat.ui.databinding.EmployeeItemBinding


class TruckDriversAdapter(
    val onItem: (TruckDriver) -> Unit
): ListAdapter<TruckDriver, TruckDriversAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.employee_item, parent, false)
        return Holder(view, onItem)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))

        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.card_appearing)
        holder.itemView.startAnimation(animation)
    }

    class Holder(view: View, private val onItem: (TruckDriver) -> Unit): RecyclerView.ViewHolder(view){
        private val binding = EmployeeItemBinding.bind(view)

        fun bind(item: TruckDriver){
            binding.apply {
                val firstName = item.firstName?:""
                val middleName = item.middleName?:""
                val surname = item.surname

                val fullName = "$firstName $middleName $surname"

                driverPhoneLayout.isGone = item.phoneNumber == null
                name.text = fullName
                phoneNumber.text = item.phoneNumber
                root.setOnClickListener {
                    onItem(item)
                }
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<TruckDriver>(){
        override fun areItemsTheSame(oldItem: TruckDriver, newItem: TruckDriver): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TruckDriver, newItem: TruckDriver): Boolean {
            return oldItem == newItem
        }
    }
}