package ru.javacat.ui.route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.YearHolder
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.adapters.OnRouteListener
import ru.javacat.ui.adapters.RoutesAdapter
import ru.javacat.ui.databinding.FragmentRouteListBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.utils.showYearCalendar

@AndroidEntryPoint
class RouteListFragment : BaseFragment<FragmentRouteListBinding>() {

    override var bottomNavViewVisibility: Int = View.VISIBLE

    private val viewModel: RouteListViewModel by viewModels()
    private lateinit var routesAdapter: RoutesAdapter
    //private var myCompany: Company? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteListBinding
        get() = { inflater, container ->
            FragmentRouteListBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("routeListFrag", "onViewCreated")
        binding.chooseYearBtn.text = YearHolder.selectedYear.toString()
        updateList()

        viewModel.getCustomerById(FragConstants.MY_COMPANY_ID)


        binding.chooseYearBtn.setOnClickListener {
            showYearCalendar {
                    selectedYear ->
                YearHolder.selectedYear = selectedYear
                binding.chooseYearBtn.text = selectedYear.toString()
                updateList()
            }
        }

        //NewRoute
        binding.newRouteBtn.setOnClickListener {
            toNewRoute()
        }


        binding.newExtendedRouteBtn.setOnClickListener {
            toNewRoute()
        }


        //Adapter
        routesAdapter = RoutesAdapter(object : OnRouteListener {
            val bundle = Bundle()
            override fun onItem(item: Route) {
                bundle.putLong(FragConstants.ROUTE_ID, item.id ?: 0L)
                findNavController().navigate(R.id.routeViewPagerFragment, bundle)

            }

            override fun onRemove(item: Route) {
                viewLifecycleOwner.lifecycleScope.launch {
                    item.id?.let { viewModel.removeRoute(it) }
                }
            }
        })

        binding.routesList.adapter = routesAdapter

        lifecycleScope.launch {
            viewModel.allRoutes.collectLatest {
                binding.noRoutesLayout.isGone = it.isNotEmpty()
                binding.newExtendedRouteBtn.isGone = it.isNotEmpty()
                binding.newRouteBtn.isGone = it.isEmpty()
                routesAdapter.submitList(it)
            }
        }

    }

    private fun updateList() {
        viewModel.getAllRoutes(YearHolder.selectedYear)
    }

    private fun toNewRoute() {
        //val bundle = Bundle()
        //if (myCompany != null) {
        findNavController().navigate(R.id.newRouteFragment)
//        } else {
//            Toast.makeText(
//                requireContext(),
//                "Заполните карточку вашей компании",
//                Toast.LENGTH_SHORT
//            ).show()
//            bundle.putLong(FragConstants.CUSTOMER_ID, FragConstants.MY_COMPANY_ID)
//            findNavController().navigate(R.id.newCustomerFragment, bundle)
//        }
    }
}