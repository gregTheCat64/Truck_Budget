package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Cargo
import ru.javacat.ui.adapters.CargoAdapter
import ru.javacat.ui.databinding.FragmentAddCargoBinding
import ru.javacat.ui.utils.AndroidUtils
import ru.javacat.ui.view_models.AddCargoViewModel

@AndroidEntryPoint
class AddCargoFragment : BaseFragment<FragmentAddCargoBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddCargoBinding
        get() = { inflater, container ->
            FragmentAddCargoBinding.inflate(inflater, container, false)
        }

    private val viewModel: AddCargoViewModel by viewModels()
    private lateinit var cargoAdapter: CargoAdapter
    private var cargosFound: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCargos()
        initAdapter()
        addEditTextListener()
            //Навигация
        loadStateListener()


        binding.addNewCargoBtn.setOnClickListener {
            val cargoName = binding.cargoEditText.text.toString()
            viewModel.insertNewCargo(Cargo(null, cargoName))
            binding.addNewCargoBtn.isGone = true
        }


    }

    private fun initAdapter() {
        cargoAdapter = CargoAdapter {
            viewModel.addCargoToOrder(it.name)
            binding.cargoEditText.setText(it.name)
            //binding.cargoRecView.isGone = true
            //binding.cargoEditText.clearFocus()
            //AndroidUtils.hideKeyboard(requireView())
        }

        binding.cargoRecView.adapter = cargoAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cargo.collectLatest {
                    // Log.i("OrderFrag", "cargos: $it")
                    cargoAdapter.submitList(it)
                    cargosFound = it?.size != 0
                }
            }
        }
    }

    private fun addEditTextListener() {
        binding.cargoEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchCargos(p0.toString())
                binding.cargoRecView.isVisible = true
                if (!cargosFound) {
                    binding.addNewCargoBtn.isVisible = true
                    binding.addNewCargoBtn.text = "Сохранить $p0"
                } else binding.addNewCargoBtn.isGone = true
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun loadStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadState.collectLatest {
                    if (it is LoadState.Success.GoForward) {
                        findNavController().navigate(R.id.addPointsFragment)
                    }
                }
            }
        }
    }
}