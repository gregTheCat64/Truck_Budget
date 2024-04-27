package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.RouteListViewModel
import java.time.LocalDate

@AndroidEntryPoint
class RouteListFragment : BaseFragment<FragmentRouteListBinding>() {

    override var bottomNavViewVisibility: Int = View.VISIBLE

    private val viewModel: RouteListViewModel by viewModels()
    private lateinit var routesAdapter: RoutesAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteListBinding
        get() = { inflater, container ->
            FragmentRouteListBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Рейсы"

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("routeListFrag", "onViewCreated")

        viewModel.getAllRoutes()

        val bundle = Bundle()

        //NewRoute
        binding.newRouteBtn.setOnClickListener {
            findNavController().navigate(R.id.newRouteFragment)

        }

        //Adapter
        routesAdapter = RoutesAdapter (object : OnRouteListener{
            override fun onItem(item: Route) {
                    //viewModel.getRouteAndUpdateEditedRoute(item.id?:0L)
                    bundle.putLong(FragConstants.ROUTE_ID, item.id?:0L)
                //val action = R.id.action_routeListFragment_to_routeFragment
                findNavController().navigate(R.id.viewPagerFragment, bundle)

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
//        lifecycleScope.launch {
//            viewLifecycleOwner.lifecycleScope.launch {
//                repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    viewModel.loadState.collectLatest {
//                        Log.i("routeListFrag", "state: ${it.toString()}")
//                        if (it == LoadState.Success.GoForward) {
//                            findNavController().navigate(R.id.routeFragment)
//                        }
//                    }
//                }
//            }
//        }
    }
}