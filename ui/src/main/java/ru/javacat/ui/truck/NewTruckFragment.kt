package ru.javacat.ui.truck

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
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.Vehicle
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentNewTransportBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class NewTruckFragment : BaseFragment<FragmentNewTransportBinding>() {

    private val viewModel: NewTruckViewModel by viewModels()
    private var transportId: Long = 0
    private var isNeedToSet: Boolean = false
    private var companyId: Long = -1L
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewTransportBinding
        get() = { inflater, container ->
            FragmentNewTransportBinding.inflate(inflater, container, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        companyId = args?.getLong(FragConstants.COMPANY_ID) ?: -1L
        //typeOfTransport = args?.getString(FragConstants.TYPE_OF_TRANSPORT) ?: "unknown"
        transportId = args?.getLong(FragConstants.TRANSPORT_ID) ?: 0
        isNeedToSet = arguments?.getBoolean(FragConstants.IS_NEED_TO_SET) ?: false
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

        binding.typeOfTransportLayout.visibility = View.GONE
        binding.typeOfTransportChipGroup.visibility = View.GONE

        if (transportId != 0L) {
            Log.i("newtransportFrag", "gettin truck by transportId: $transportId")
            viewModel.getTruckById(transportId)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedTruck.collectLatest {
                    if (it != null) {
                        updateUi(it)
                    }
                }
            }
        }


        binding.typeOfTransportChipGroup.setOnCheckedStateChangeListener { chipGroup, list ->
            val checkedId = chipGroup.checkedChipId
            val chip = chipGroup.findViewById<Chip>(checkedId)
            binding.typeOfTransportEt.setText(chip.text?.toString())
        }

        binding.actionBar.saveBtn.setOnClickListener {
            saveNewTruck()
        }

        binding.actionBar.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadState.collectLatest {
                    when (it) {
                        is LoadState.Success.GoBack -> {
                            findNavController().navigateUp()
                        }

                        is LoadState.Success.Created -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.created),
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }

                        is LoadState.Loading -> {
                            binding.progressBar.isGone = false
                        }

                        is LoadState.Success.Removed -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.removed),
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }

                        else -> {
//
                        }
                    }
                }
            }
        }

    }


    private fun updateUi(transport: Vehicle) {
        //(activity as AppCompatActivity).supportActionBar?.title = transport.nameToShow
        binding.actionBar.title.text = getString(R.string.edit)
        Log.i("newTransportFragm", "vehicle: $transport")

        transport.apply {
            vin?.let {
                binding.vin.setText(it)
            }
            model?.let {
                binding.modelOfVehicle.setText(it)
            }
            yearOfManufacturing?.let {
                binding.yearOfManufacturing.setText(it)
            }
        }
        binding.apply {
            regNumber.setText(transport.regNumber)
            regCode.setText(transport.regionCode.toString())
        }
    }

    private fun saveNewTruck() {
        val regNumber = binding.regNumber.text.toString()
        val regionCode = if (binding.regCode.text?.isNotEmpty() == true) {
            binding.regCode.text?.toString()?.toInt() ?: 0
        } else {
            0
        }

        val vin = binding.vin.text.toString()
        val model = binding.modelOfVehicle.text.toString()
        val year = binding.yearOfManufacturing.text.toString()
        val type = binding.typeOfTransportEt.text.toString()


        val newVehicle = Truck(
            transportId, false, companyId, regNumber, regionCode, vin, model, year
        )

        if (regNumber.isNotEmpty() && regionCode.toString().isNotEmpty()) {
            viewModel.insertNewTruck(newVehicle, isNeedToSet)
        } else Toast.makeText(
            requireContext(),
            getString(R.string.fill_requested_fields),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun removeTransport(id: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            //viewModel.hideTruckById(id)
        }

    }
}