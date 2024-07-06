package ru.javacat.ui.edit_order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.CargoName
import ru.javacat.ui.R
import ru.javacat.ui.adapters.CargoAdapter
import ru.javacat.ui.adapters.CargoChipAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding

@AndroidEntryPoint
class EditCargoDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseItemWithSearchBinding
    private lateinit var cargoAdapter: CargoAdapter
    private val viewModel: EditCargoDialogViewModel by viewModels()

    private var cargosFound: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseItemWithSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCargos()
        addEditTextListener()

        cargoAdapter = CargoAdapter {
            viewModel.addCargoToOrder(it)
            this.dismiss()
        }
        binding.itemRecView.adapter = cargoAdapter
        binding.labelTv.text = getString(R.string.cargo)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.cargoList.collectLatest {
                    cargoAdapter.submitList(it)
                    cargosFound = it?.isNotEmpty() == true
                }
            }
        }

        binding.saveBtn.setOnClickListener {
            val newCargo = binding.searchEditText.text.toString().trim()
            if (!cargosFound && newCargo.isNotEmpty()) {
                viewModel.insertNewCargo(CargoName(nameToShow = newCargo))
                viewModel.addCargoToOrder(CargoName(nameToShow = newCargo))
                this.dismiss()
            } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addEditTextListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchCargos(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
    }
