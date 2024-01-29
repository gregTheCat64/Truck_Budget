package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.javacat.domain.models.Staff
import ru.javacat.ui.databinding.FragmentNewDriverBinding

class NewDriverFragment: BaseFragment<FragmentNewDriverBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewDriverBinding
        get() = {inflater, container ->
            FragmentNewDriverBinding.inflate(inflater, container,  false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveBtn.setOnClickListener {
            val fullName = binding.staffName.text.toString()
            val passSerial = binding.passSerial.text.toString()
            val passNumber = binding.passNumber.text.toString()
            val passWhen = binding.passWhen.text.toString()
            val passWhere = binding.passWhere.text.toString()
            val driveLicenseNumber = binding.driveLicenseNumber.text.toString()
            val address = binding.address.text.toString()

            val id = passSerial.toString()+passNumber.toString()

            val newDriver = Staff(
                id, fullName, passSerial,passNumber,passWhen,
                passWhere,driveLicenseNumber,address
            )
        }
    }
}