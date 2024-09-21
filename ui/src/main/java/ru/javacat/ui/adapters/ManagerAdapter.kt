package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.domain.models.Manager
import ru.javacat.ui.R
import ru.javacat.ui.databinding.EmployeeItemBinding

interface OnManagerListener {
    fun onManager(item: Manager)
    fun onPhone(item: String?)
    fun onWhatsapp(item: String?)
}
class ManagerAdapter(
    private val onManagerListener: OnManagerListener
): ListAdapter<Manager, ManagerAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.employee_item, parent, false)
        return Holder(view, onManagerListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(view: View, private val onManagerListener: OnManagerListener): RecyclerView.ViewHolder(view){
        private val binding = EmployeeItemBinding.bind(view)

        fun bind(item: Manager){
            binding.apply {
                name.text = item.nameToShow
                phoneNumber.text = item.phoneNumber
                driverPhoneLayout.isGone = item.phoneNumber == null
                root.setOnClickListener {
                    onManagerListener.onManager(item)
                }
                phoneNumber.setOnClickListener {
                    onManagerListener.onPhone(phoneNumber.text.toString())
                }
                whatsappMsgBtn.setOnClickListener {
                    onManagerListener.onWhatsapp(phoneNumber.text.toString())
                }
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<Manager>(){
        override fun areItemsTheSame(oldItem: Manager, newItem: Manager): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Manager, newItem: Manager): Boolean {
            return oldItem == newItem
        }
    }
}