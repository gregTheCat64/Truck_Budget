package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_cancel, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.cancel_button_menu_item -> {
                        //TODO добавить диалог!

                        findNavController().navigateUp()
                        return true
                    }
                    android.R.id.home -> {

                        return true
                    }
                    else ->  return false
                }
            }
        }, viewLifecycleOwner)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val companyId = args?.getLong(FragConstants.COMPANY_ID)?:-1L

        val truckDriverId = args?.getLong(FragConstants.DRIVER_ID)

        Log.i("NewDriverFrag", "companyId: $companyId")
        Log.i("NewDriverFrag", "driverId: $truckDriverId")

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                if (truckDriverId != null) {
                    viewModel.getTruckDriverById(truckDriverId)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedTruckDriver.collectLatest {
                    if (it != null) {
                        updateUi(it)
                    }
                }
            }
        }

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
                truckDriverId?:0,0,companyId,firstName, middleName, surname, passportData, passWhen,
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
                        //findNavController().navigate(R.id.truckDriversListFragment)
                    }
                }
            }
        }
    }

    private fun updateUi(truckDriver: TruckDriver){
        binding.apply {
            (activity as AppCompatActivity).supportActionBar?.title = truckDriver.nameToShow
            surName.setText(truckDriver.surname)
            middleName.setText(truckDriver.middleName)
            firstName.setText(truckDriver.firstName)
            //TODO разделить серию и номер
            //passSerial.setText(truckDriver.pa)
            passNumber.setText(truckDriver.passportNumber)
            passWhen.setText(truckDriver.passportReceivedDate.toString())
            passWhere.setText(truckDriver.passportReceivedPlace)
            driveLicenseNumber.setText(truckDriver.driveLicenseNumber)
            address.setText(truckDriver.placeOfRegistration)
            phoneNumber.setText(truckDriver.phoneNumber)
            phoneNumber2.setText(truckDriver.secondNumber)

        }
    }
}