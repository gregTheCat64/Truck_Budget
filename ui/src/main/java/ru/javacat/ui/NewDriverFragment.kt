package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.TruckDriver
import ru.javacat.ui.databinding.FragmentNewDriverBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.view_models.NewDriverViewModel

@AndroidEntryPoint
class NewDriverFragment : BaseFragment<FragmentNewDriverBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: NewDriverViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewDriverBinding
        get() = { inflater, container ->
            FragmentNewDriverBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val companyId = args?.getLong(FragConstants.COMPANY_ID)?:-1L

        binding.passWhen.setOnClickListener {
            parentFragmentManager.showCalendar {
            }
        }

        binding.saveBtn.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val middleName = binding.middleName.text.toString()
            val surname = binding.surName.text.toString()
            val passSerial = binding.passSerial.text.toString()
            val passNumber = binding.passNumber.text.toString()
            val passWhen = binding.passWhen.text.toString()
            val passWhere = binding.passWhere.text.toString()
            val driveLicenseNumber = binding.driveLicenseNumber.text.toString()
            val address = binding.address.text.toString()
            val phoneNumber = binding.phoneNumber.text.toString()

            val passportData = "$passSerial $passNumber"

            //val id = passSerial.toString()+passNumber.toString()

            //TODO добавить поле для 2 номера тел.
            val newDriver = TruckDriver(
                0,0,companyId,firstName, middleName, surname, passportData, passWhen,
                passWhere, driveLicenseNumber, address, phoneNumber, "",""
            )

            viewLifecycleOwner.lifecycleScope.launch {
                if (surname.isNotEmpty()){
                    viewModel.insertNewDriver(newDriver)
                } else Toast.makeText(requireContext(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show()

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    if (it == LoadState.Success.GoBack){
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }
}