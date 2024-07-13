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
import androidx.core.view.isGone
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


    private val viewModel: NewDriverViewModel by viewModels()

    private var isNeedToSet: Boolean = false
    private var truckDriverId: Long? = null
    private var companyId: Long = -1L
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewDriverBinding
        get() = { inflater, container ->
            FragmentNewDriverBinding.inflate(inflater, container, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        companyId = args?.getLong(FragConstants.COMPANY_ID)?:-1L
        isNeedToSet = arguments?.getBoolean(FragConstants.IS_NEED_TO_SET)?:false
        truckDriverId = args?.getLong(FragConstants.DRIVER_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_remove, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    R.id.remove_menu_item -> {
                        truckDriverId?.let { removeDriver(it) }
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


        Log.i("NewDriverFrag", "companyId: $companyId")
        Log.i("NewDriverFrag", "driverId: $truckDriverId")

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                if (truckDriverId != 0L) {
                    viewModel.getTruckDriverById(truckDriverId!!)
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
                    viewModel.insertNewDriver(newDriver, isNeedToSet)
                } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {

                    when (it) {
                        is LoadState.Success.Created -> {
                            findNavController().navigateUp()
                            Toast.makeText(requireContext(),
                                getString(R.string.created), Toast.LENGTH_SHORT).show()
                        }
                        is LoadState.Loading -> {
                            binding.progressBar.isGone = false
                        }
                        is LoadState.Success.Removed -> {
                            findNavController().navigateUp()
                            Toast.makeText(requireContext(), getString(R.string.removed), Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
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

    private fun removeDriver(id: Long){
        Log.i("NewDriverFrag", "driverId: $id")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.removeDriverById(id)
        }
    }
}