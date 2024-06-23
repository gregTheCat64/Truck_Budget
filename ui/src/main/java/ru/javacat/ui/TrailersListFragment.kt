package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.TrailersAdapter
import ru.javacat.ui.databinding.FragmentTrailersListBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.TrailersViewModel
import ru.javacat.ui.view_models.TruckFleetViewPagerViewModel

@AndroidEntryPoint
class TrailersListFragment: BaseFragment<FragmentTrailersListBinding>() {
    private val viewModel: TruckFleetViewPagerViewModel by activityViewModels()
    private lateinit var trailersAdapter: TrailersAdapter

    override var bottomNavViewVisibility: Int = View.GONE
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
            bundle.putString(FragConstants.TYPE_OF_TRANSPORT, "TRAILER")
            findNavController().navigate(R.id.newTransportFragment, bundle)
        }

        binding.trailerRecView.adapter = trailersAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trailers.collectLatest {
                    trailersAdapter.submitList(it)
                }
            }
        }
    }
}

