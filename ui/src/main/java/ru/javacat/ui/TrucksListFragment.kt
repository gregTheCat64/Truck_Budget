package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.TrucksAdapter
import ru.javacat.ui.databinding.FragmentTrucksListBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.TruckFleetViewPagerViewModel
import ru.javacat.ui.view_models.TrucksViewModel


@AndroidEntryPoint
class TrucksListFragment: BaseFragment<FragmentTrucksListBinding>() {

    private val viewModel: TruckFleetViewPagerViewModel by activityViewModels()
    private lateinit var trucksAdapter: TrucksAdapter

    override var bottomNavViewVisibility: Int = View.GONE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentTrucksListBinding
        get() = {inflater, container ->
            FragmentTrucksListBinding.inflate(inflater, container, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trucksAdapter = TrucksAdapter {
            Log.i("TrucksListFrag", "adapter clicked")
            val bundle = Bundle()
            bundle.putLong(FragConstants.COMPANY_ID, it.companyId)
            bundle.putLong(FragConstants.TRANSPORT_ID, it.id)
            bundle.putString(FragConstants.TYPE_OF_TRANSPORT, "TRUCK")
            findNavController().navigate(R.id.newTransportFragment, bundle)
        }
        binding.trucksRecView.adapter = trucksAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trucks.collectLatest {
                    trucksAdapter.submitList(it)
                }
            }
        }

        binding.addTruckBtn.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putLong(FragConstants.COMPANY_ID, compa)
//            bundle.putLong(FragConstants.TRANSPORT_ID, 0)
//            bundle.putString(FragConstants.TYPE_OF_TRANSPORT, "TRUCK")
//            findNavController().navigate(R.id.newTransportFragment, bundle)
        }

    }

}