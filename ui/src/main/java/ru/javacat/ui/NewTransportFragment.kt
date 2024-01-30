package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.domain.models.Vehicle
import ru.javacat.ui.databinding.FragmentNewTransportBinding
import ru.javacat.ui.view_models.NewTransportViewModel

@AndroidEntryPoint
class NewTransportFragment: BaseFragment<FragmentNewTransportBinding>() {

    private val viewModel: NewTransportViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewTransportBinding
        get() = {inflater, container->
            FragmentNewTransportBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveBtn.setOnClickListener {
            val type = binding.typeOfVehicle.text.toString()
            val regNumber = binding.regNumber.text.toString()
            val vin = binding.vin.text.toString()
            val model = binding.modelOfVehicle.text.toString()
            val year = binding.yearOfManufacturing.text.toString()

            val newVehicle = Vehicle(
                0, regNumber,vin,model,type,year
            )
            viewModel.insertNewVehicle(newVehicle)
        }
    }
}