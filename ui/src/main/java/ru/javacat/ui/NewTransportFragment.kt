package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.ui.databinding.FragmentNewTransportBinding
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
        val item = args?.getString("item")?:"unknown"

        binding.saveBtn.setOnClickListener {
            val regNumber = binding.regNumber.text.toString()
            val regionCode = binding.regCode.text?.toString()?.toInt()?:0
            val vin = binding.vin.text.toString()
            val model = binding.modelOfVehicle.text.toString()
            val year = binding.yearOfManufacturing.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.loadState.collectLatest {
                        if (it== LoadState.Success.GoBack){
                            findNavController().navigateUp()
                        }
                    }
                }
            }

            when (item){
                "TRUCK" -> {
                    val newVehicle = Truck(
                        0, regNumber,regionCode, vin,model, year
                    )
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.insertNewTruck(newVehicle)
                    }

                }
                "TRAILER" ->{
                    val newVehicle = Trailer(
                        0, regNumber,regionCode, vin,model, year
                    )
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.insertNewTrailer(newVehicle)
                    }

                }
            }

        }
    }
}