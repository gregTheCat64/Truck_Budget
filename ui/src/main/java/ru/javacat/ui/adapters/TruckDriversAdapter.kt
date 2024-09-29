package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.javacat.common.utils.toPrettyNumber
import ru.javacat.domain.models.Manager
import ru.javacat.domain.models.TruckDriver
import ru.javacat.ui.R
import ru.javacat.ui.databinding.EmployeeItemBinding

interface OnTruckDriverListener {
    fun onItem(item: TruckDriver)
    fun onPhone(item: String?)
    fun onWhatsapp(item: String?)
}

class TruckDriversAdapter(
    private val onTruckDriverListener: OnTruckDriverListener
): ListAdapter<TruckDriver, TruckDriversAdapter.Holder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.employee_item, parent, false)
        return Holder(view, onTruckDriverListener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))

        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.card_appearing)
        holder.itemView.startAnimation(animation)
    }

    class Holder(view: View, private val onTruckDriverListener: OnTruckDriverListener): RecyclerView.ViewHolder(view){
        private val binding = EmployeeItemBinding.bind(view)

        fun bind(item: TruckDriver){
            binding.apply {
                val firstName = item.firstName?:""
                val middleName = item.middleName?:""
                val surname = item.surname

                val fullName = "$firstName $middleName $surname"

                driverPhoneLayout.isGone = item.phoneNumber == null
                name.text = fullName
                phoneNumber.text = item.phoneNumber?.toPrettyNumber()
                root.setOnClickListener {
                    onTruckDriverListener.onItem(item)
                }
                phoneNumberBtn.setOnClickListener {
                    onTruckDriverListener.onPhone(phoneNumber.text.toString())
                }
                whatsappMsgBtn.setOnClickListener {
                    onTruckDriverListener.onWhatsapp(phoneNumber.text.toString())
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