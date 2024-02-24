package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.OnRouteListener
import ru.javacat.ui.adapters.RoutesAdapter
import ru.javacat.ui.databinding.FragmentRouteListBinding
import ru.javacat.ui.view_models.RouteListViewModel
import java.time.LocalDate

@AndroidEntryPoint
class RouteListFragment : BaseFragment<FragmentRouteListBinding>() {

    private val viewModel: RouteListViewModel by viewModels()
    private lateinit var routesAdapter: RoutesAdapter


    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteListBinding
        get() = { inflater, container ->
            FragmentRouteListBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //NewRoute
        binding.newRouteBtn.setOnClickListener {
            Log.i("MyTag", "clicked")
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    //viewModel.clearEditedRoute()
                    viewModel.insertNewRoute(Route())
                }
            }
        }

        //Adapter
        routesAdapter = RoutesAdapter (object : OnRouteListener{
            override fun onItem(item: Route) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getRouteAndUpdateEditedRoute(item.id?:0L)
                }
            }

            override fun onRemove(item: Route) {
                viewLifecycleOwner.lifecycleScope.launch{
                    item.id?.let { viewModel.removeRoute(it) }
                }
            }
        })
        binding.routesList.adapter = routesAdapter

        lifecycleScope.launch {
            viewModel.allRoutes.collectLatest {
                routesAdapter.submitList(it)
            }
        }

        //navigation
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.loadState.collectLatest {
                        if (it == LoadState.Success) {
                            findNavController().navigate(R.id.routeFragment)
                        }
                    }
                }
            }
        }
    }
}