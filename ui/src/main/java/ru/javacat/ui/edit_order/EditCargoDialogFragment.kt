package ru.javacat.ui.edit_order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.BaseNameModel
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.CargoName
import ru.javacat.ui.R
import ru.javacat.ui.adapters.CargoAdapter
import ru.javacat.ui.adapters.CargoChipAdapter
import ru.javacat.ui.adapters.my_adapter.OnModelWithRemoveBtnListener
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

        cargoAdapter = CargoAdapter(object : OnModelWithRemoveBtnListener{
            override fun onItem(model: BaseNameModel<Long>) {
                viewModel.addCargoToOrder(model.nameToShow)
                dismiss()
            }

            override fun onRemove(model: BaseNameModel<Long>) {
                model.id?.let { viewModel.removeCargo(it) }
                //dismiss()
            }
        })

        val cargosLayoutManager = FlexboxLayoutManager(requireContext())
        cargosLayoutManager.flexDirection = FlexDirection.ROW
        cargosLayoutManager.justifyContent = JustifyContent.FLEX_START
        binding.itemRecView.layoutManager = cargosLayoutManager

        binding.itemRecView.adapter = cargoAdapter
        binding.labelTv.text = getString(R.string.cargo)
        binding.searchEditText.hint = "Введите название груза"

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
            if (newCargo.isNotEmpty()) {
                viewModel.insertNewCargo(CargoName(nameToShow = newCargo))
                viewModel.addCargoToOrder(newCargo)
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
                binding.saveBtn.isVisible = !p0.isNullOrEmpty()
                binding.saveBtn.text = "Создать груз: $p0"
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
    }
