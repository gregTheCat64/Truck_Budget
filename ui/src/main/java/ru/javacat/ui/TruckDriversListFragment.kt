package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.TruckDriversAdapter
import ru.javacat.ui.databinding.FragmentTruckDriversListBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.TruckDriversViewModel
import ru.javacat.ui.view_models.TruckFleetViewPagerViewModel

@AndroidEntryPoint
class TruckDriversListFragment: BaseFragment<FragmentTruckDriversListBinding>() {
    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: TruckFleetViewPagerViewModel by activityViewModels()
    private lateinit var truckDriversAdapter: TruckDriversAdapter

    var companyId: Long? = -1

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