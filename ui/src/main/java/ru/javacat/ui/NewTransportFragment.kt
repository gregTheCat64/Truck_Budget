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

    private val viewModel: NewTransportViewModel by viewModels()
    private var typeOfTransport = ""
    private var transportId: Long = 0
    private var isNeedToSet: Boolean = false
    private var companyId: Long = -1L
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewTransportBinding
        get() = {inflater, container->
            FragmentNewTransportBinding.inflate(inflater, container, false)
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
                    R.id.remove_menu_item -> {
                        removeTransport(typeOfTransport, transportId)
                        findNavController().navigateUp()
                        return true
                    }
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    else ->  return false
                }
            }
        }, viewLifecycleOwner)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        companyId = args?.getLong(FragConstants.COMPANY_ID)?:-1L
        typeOfTransport = args?.getString(FragConstants.TYPE_OF_TRANSPORT)?:"unknown"
        transportId = args?.getLong(FragConstants.TRANSPORT_ID)?:0
        isNeedToSet = arguments?.getBoolean(FragConstants.IS_NEED_TO_SET)?:false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        when (typeOfTransport){
            "TRUCK" -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED){
                        Log.i("newtransportFrag","gettin truck by transportId: $transportId")
                        viewModel.getTruckById(transportId!!)

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
                        when (it) {
                            is LoadState.Success.GoBack -> {
                                findNavController().navigateUp()
                            }
                            is LoadState.Success.Created -> {
                                Toast.makeText(requireContext(), getString(R.string.created), Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }
                            is LoadState.Success.Removed -> {
                                Toast.makeText(requireContext(), getString(R.string.removed), Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }
                            else -> {
                                Toast.makeText(requireContext(), "Something wrong", Toast.LENGTH_SHORT).show()
                            }
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
                        viewModel.insertNewTruck(newVehicle, isNeedToSet)
                    }

                }
                "TRAILER" ->{
                    val newVehicle = Trailer(
                        transportId, companyId, regNumber,regionCode, vin,model, year, type
                    )
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.insertNewTrailer(newVehicle, isNeedToSet)
                    }
                }
            }

        }
    }

    private fun updateUi(transport: Vehicle){
        (activity as AppCompatActivity).supportActionBar?.title = transport.nameToShow
        Log.i("newTransportFragm", "vehicle: $transport")
        binding.apply {
            regNumber.setText(transport.regNumber)
            regCode.setText(transport.regionCode.toString())
            vin.setText(transport.vin.toString())
            modelOfVehicle.setText(transport.model.toString())
            yearOfManufacturing.setText(transport.yearOfManufacturing.toString())
        }
    }

    private fun removeTransport(typeOfTransport: String, id: Long){
        when (typeOfTransport) {
            "TRUCK" -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.removeTruckById(id)
                }
            }
            "TRAILER" -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.removeTrailerById(id)
                }
            }
        }
    }
}