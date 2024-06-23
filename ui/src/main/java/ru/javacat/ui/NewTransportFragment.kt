package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.Vehicle
import ru.javacat.ui.databinding.FragmentNewTransportBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.NewTransportViewModel

@AndroidEntryPoint
class NewTransportFragment: BaseFragment<FragmentNewTransportBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: NewTransportViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewTransportBinding
        get() = {inflater, container->
            FragmentNewTransportBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val companyId = args?.getLong(FragConstants.COMPANY_ID)?:-1L
        val typeOfTransport = args?.getString(FragConstants.TYPE_OF_TRANSPORT)?:"unknown"
        val transportId = args?.getLong(FragConstants.TRANSPORT_ID)?:0

        when (typeOfTransport){
            "TRUCK" -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED){
                        Log.i("newtransportFrag","gettin truck by transportId: $transportId")
                        viewModel.getTruckById(transportId)

                        binding.typeOfTransportLayout.visibility = View.GONE
                        binding.typeOfTransportChipGroup.visibility = View.GONE
                    }
                }
            }
            "TRAILER" -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED){
                        Log.i("newtransportFrag","gettin trailer by transportId: $transportId")
                        viewModel.getTrailerById(transportId)
                        binding.typeOfTransportLayout.visibility = View.VISIBLE
                        binding.typeOfTransportChipGroup.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedTruck.collectLatest {
                    if (it != null) {
                        updateUi(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedTrailer.collectLatest {
                    if (it != null) {
                        updateUi(it)
                    }
                }
            }
        }

        binding.typeOfTransportChipGroup.setOnCheckedStateChangeListener{chipGroup, list->
            val checkedId = chipGroup.checkedChipId
            val chip = chipGroup.findViewById<Chip>(checkedId)
            binding.typeOfTransportEt.setText(chip.text?.toString())
        }

        binding.saveBtn.setOnClickListener {
            val regNumber = binding.regNumber.text.toString()
            val regionCode = binding.regCode.text?.toString()?.toInt()?:0
            val vin = binding.vin.text.toString()
            val model = binding.modelOfVehicle.text.toString()
            val year = binding.yearOfManufacturing.text.toString()
            val type = binding.typeOfTransportEt.text.toString()


            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.loadState.collectLatest {
                        if (it== LoadState.Success.GoBack){
                            findNavController().navigateUp()
                        }
                    }
                }
            }

            when (typeOfTransport){
                "TRUCK" -> {
                    val newVehicle = Truck(
                        transportId, companyId, regNumber,regionCode, vin,model, year
                    )
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.insertNewTruck(newVehicle)
                    }

                }
                "TRAILER" ->{
                    val newVehicle = Trailer(
                        transportId, companyId, regNumber,regionCode, vin,model, year, type
                    )
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.insertNewTrailer(newVehicle)
                    }

                }
            }

        }
    }

    private fun updateUi(transport: Vehicle){
        Log.i("newTransportFragm", "vehicle: $transport")
        binding.apply {
            regNumber.setText(transport.regNumber)
            regCode.setText(transport.regionCode.toString())
            vin.setText(transport.vin.toString())
            modelOfVehicle.setText(transport.model.toString())
            yearOfManufacturing.setText(transport.yearOfManufacturing.toString())
        }
    }
}