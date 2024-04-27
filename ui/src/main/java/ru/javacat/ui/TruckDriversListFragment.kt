package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Employee
import ru.javacat.ui.adapters.EmployeesAdapter
import ru.javacat.ui.adapters.OnEmployeeListener
import ru.javacat.ui.adapters.TruckDriversAdapter
import ru.javacat.ui.databinding.FragmentTruckDriversListBinding
import ru.javacat.ui.view_models.TruckDriversVewModel

@AndroidEntryPoint
class TruckDriversListFragment: BaseFragment<FragmentTruckDriversListBinding>() {
    override var bottomNavViewVisibility: Int
        get() = super.bottomNavViewVisibility
        set(value) {
            View.GONE}

    private val viewModel: TruckDriversVewModel by viewModels()
    private lateinit var truckDriversAdapter: TruckDriversAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentTruckDriversListBinding
        get() = {inflater, container ->
            FragmentTruckDriversListBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Truck Drivers"
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDriverList()

        truckDriversAdapter = TruckDriversAdapter{

        }
        binding.driversRecView.adapter = truckDriversAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.drivers.collectLatest {
                    truckDriversAdapter.submitList(it)
                }
            }
        }

    }
}