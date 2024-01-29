package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Staff
import ru.javacat.ui.databinding.FragmentNewDriverBinding
import ru.javacat.ui.view_models.NewDriverViewModel

class NewDriverFragment: BaseFragment<FragmentNewDriverBinding>() {

    private val viewModel: NewDriverViewModel by viewModels()
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

            //val id = passSerial.toString()+passNumber.toString()

            val newDriver = Staff(
                0, fullName, passSerial,passNumber,passWhen,
                passWhere,driveLicenseNumber,address
            )

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.addDriver(newDriver)
                }
            }
        }
    }
}