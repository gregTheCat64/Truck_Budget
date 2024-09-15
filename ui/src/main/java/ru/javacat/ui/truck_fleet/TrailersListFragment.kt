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
import ru.javacat.ui.adapters.TrailersAdapter
import ru.javacat.ui.databinding.FragmentTrailersListBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class TrailersListFragment: BaseFragment<FragmentTrailersListBinding>() {
    private val viewModel: TruckFleetViewPagerViewModel by activityViewModels()
    private lateinit var trailersAdapter: TrailersAdapter

    var companyId: Long = -1

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentTrailersListBinding
        get() = { inflater, container ->
            FragmentTrailersListBinding.inflate(inflater, container, false)
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        trailersAdapter = TrailersAdapter {
            val bundle = Bundle()
            bundle.putLong(FragConstants.COMPANY_ID, it.companyId)
            bundle.putLong(FragConstants.TRANSPORT_ID, it.id)
            //bundle.putString(FragConstants.TYPE_OF_TRANSPORT, "TRAILER")
            findNavController().navigate(R.id.trailerInfoFragment, bundle)
        }

        binding.trailerRecView.adapter = trailersAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trailers.collectLatest {
                    trailersAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentCompanyId.collectLatest {
                companyId = it?:-1L
                Log.i("TruckListFrag ", "companyIdInTrailerList: $it")
            }
        }

        binding.addTrailerBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(FragConstants.COMPANY_ID, companyId)
            findNavController().navigate(R.id.newTrailerFragment, bundle)
        }
    }
}

