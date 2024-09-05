package ru.javacat.ui.truck_fleet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.adapters.TruckDriversAdapter
import ru.javacat.ui.databinding.FragmentTruckDriversListBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class TruckDriversListFragment: BaseFragment<FragmentTruckDriversListBinding>() {

    private val viewModel: TruckFleetViewPagerViewModel by activityViewModels()
    private lateinit var truckDriversAdapter: TruckDriversAdapter

    var companyId: Long = -1

    private var currentRoute : Route? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentTruckDriversListBinding
        get() = {inflater, container ->
            FragmentTruckDriversListBinding.inflate(inflater, container, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("DriverListFrag", "onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("DriverListFrag", "onViewCreated")

        truckDriversAdapter = TruckDriversAdapter{
            val bundle = Bundle()
            bundle.putLong(FragConstants.COMPANY_ID, it.customerId)
            bundle.putLong(FragConstants.DRIVER_ID, it.id)
            findNavController().navigate(R.id.newDriverFragment, bundle)

        }
        binding.driversRecView.adapter = truckDriversAdapter

        viewLifecycleOwner.lifecycleScope.launch {
                viewModel.drivers.collectLatest {
                    truckDriversAdapter.submitList(it)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentCompanyId.collectLatest {
                companyId = it?:-1L
                Log.i("TruckDriverListFrag ", "companyIdInDriversList: $it")
            }
        }

        binding.addDriverBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(FragConstants.COMPANY_ID, companyId)
            findNavController().navigate(R.id.newDriverFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("DriverListFrag", "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("DriverListFrag", "onDestroy")
    }
}