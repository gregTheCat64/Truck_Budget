package ru.javacat.ui.truck_fleet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.adapters.TrucksAdapter
import ru.javacat.ui.databinding.FragmentTrucksListBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.TruckFleetViewPagerViewModel


@AndroidEntryPoint
class TrucksListFragment: BaseFragment<FragmentTrucksListBinding>() {

    private val viewModel: TruckFleetViewPagerViewModel by activityViewModels()
    private lateinit var trucksAdapter: TrucksAdapter

    var companyId: Long = -1

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentTrucksListBinding
        get() = {inflater, container ->
            FragmentTrucksListBinding.inflate(inflater, container, false)
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trucksAdapter = TrucksAdapter {
            Log.i("TrucksListFrag", "adapter clicked")
            val bundle = Bundle()
            bundle.putLong(FragConstants.COMPANY_ID, it.companyId)
            bundle.putLong(FragConstants.TRANSPORT_ID, it.id)
            bundle.putString(FragConstants.TYPE_OF_TRANSPORT, "TRUCK")
            findNavController().navigate(R.id.newTruckFragment, bundle)
        }
        binding.trucksRecView.adapter = trucksAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trucks.collectLatest {
                    trucksAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentCompanyId.collectLatest {
                companyId = it?:-1L
                Log.i("TruckListFrag ", "companyIdInTruckList: $it")
            }
        }

        binding.addTruckBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(FragConstants.TYPE_OF_TRANSPORT, "TRUCK")
            bundle.putLong(FragConstants.COMPANY_ID, companyId)
        }

    }

}