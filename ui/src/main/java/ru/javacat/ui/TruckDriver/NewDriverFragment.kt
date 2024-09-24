package ru.javacat.ui.TruckDriver

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
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentNewDriverBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showCalendar
import java.time.LocalDate

@AndroidEntryPoint
class NewDriverFragment : BaseFragment<FragmentNewDriverBinding>() {
    private val viewModel: NewDriverViewModel by viewModels()

    private var isNeedToSet: Boolean = false
    private var truckDriverId: Long? = null
    private var companyId: Long = -1L

    private var newPassWhen: LocalDate? = null

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

        (activity as AppCompatActivity).supportActionBar?.hide()
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.i("NewDriverFrag", "companyId: $companyId")
        Log.i("NewDriverFrag", "driverId: $truckDriverId")

        truckDriverId?.let { viewModel.getTruckDriver(it) }


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
                newPassWhen = it
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

        binding.actionBar.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.actionBar.saveBtn.setOnClickListener {
            saveNewDriver()
        }
    }

    private fun saveNewDriver(){
        val firstName = binding.firstName.text?.toString().takeIf { it?.isNotEmpty() == true }
        val middleName = binding.middleName.text?.toString().takeIf { it?.isNotEmpty() == true }
        val surname = binding.surName.text.toString()
        val passData = binding.passData.text?.toString().takeIf { it?.isNotEmpty() == true }
        //val passNumber = binding.passNumber.text?.toString().takeIf { it?.isNotEmpty() == true }
        val passWhen = newPassWhen
        val passWhere = binding.passWhere.text?.toString().takeIf { it?.isNotEmpty() == true }
        val driveLicenseNumber = binding.driveLicenseNumber.text?.toString().takeIf { it?.isNotEmpty() == true }
        val address = binding.address.text?.toString().takeIf { it?.isNotEmpty() == true }
        val phoneNumber = binding.phoneNumber.text?.toString().takeIf { it?.isNotEmpty() == true }
        val comment = binding.comment.text?.toString().takeIf { it?.isNotEmpty() == true }



        //val id = passSerial.toString()+passNumber.toString()

        val newDriver = TruckDriver(
            truckDriverId?:0,0,false,companyId,firstName, middleName, surname, passData, passWhen,
            passWhere, driveLicenseNumber, address, phoneNumber, "",comment,null
        )

        if (surname.isNotEmpty()){
            viewModel.insertNewDriver(newDriver, isNeedToSet)
        } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
    }

    private fun updateUi(truckDriver: TruckDriver){
        //(activity as AppCompatActivity).supportActionBar?.title = truckDriver.nameToShow
        binding.actionBar.title.text = getString(R.string.edit)
        newPassWhen = truckDriver.passportReceivedDate

        binding.apply {
            surName.setText(truckDriver.surname)
            middleName.setText(truckDriver.middleName)
            firstName.setText(truckDriver.firstName)
            passData.setText(truckDriver.passportNumber)
            passWhen.setText(truckDriver.passportReceivedDate?.toString())
            passWhere.setText(truckDriver.passportReceivedPlace)
            driveLicenseNumber.setText(truckDriver.driveLicenseNumber)
            address.setText(truckDriver.placeOfRegistration)
            phoneNumber.setText(truckDriver.phoneNumber)
            phoneNumber2.setText(truckDriver.secondNumber)
            comment.setText(truckDriver.comment)
        }
    }
}