package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.RouteViewPagerAdapter
import ru.javacat.ui.databinding.FragmentRouteViewPagerBinding

import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.RouteViewPagerViewModel

@AndroidEntryPoint
class RouteViewPagerFragment:BaseFragment<FragmentRouteViewPagerBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteViewPagerBinding
        get() = {inflater, container->
            FragmentRouteViewPagerBinding.inflate(inflater,container,false)
        }

    private val viewModel: RouteViewPagerViewModel by viewModels()

    var currentFragment: Int? = 0
    private var routeId: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("RouteVPFrag", "oncreate")


        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        (activity as AppCompatActivity).supportActionBar?.title = "TruckBudget"

        val args = arguments
        routeId = args?.getLong(FragConstants.ROUTE_ID)


        Log.i("RouteVPfrag", "routeId: $routeId")
        val bundle =Bundle ()
        bundle.putLong(FragConstants.ROUTE_ID, routeId?:0)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) findNavController().navigateUp()
//        return true
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("RouteVPFrag", "onCreateView")

        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_review, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.remove -> {
                        routeId?.let { removeRoute(it) }
                        //findNavController().navigateUp()
                        return true
                    }
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    else ->  return false
                }
            }
        }, viewLifecycleOwner)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("RouteVPFrag", "onViewCreated")

        viewModel.clearEditedOrder()

        val fraglist = listOf(
            RouteFragment(),
            RouteCalculationInfoFragment()
        )

        val ordersWord = getString(R.string.orders)
        val countWord = getString(R.string.count)

        val fragTitles = listOf(
            ordersWord,
            countWord
        )

        stateListener()

        val adapter = RouteViewPagerAdapter(requireActivity(), fraglist)
        binding.routeViewPager.adapter = adapter

        TabLayoutMediator(binding.routeTabLayout, binding.routeViewPager){
            tab, pos-> tab.text = fragTitles[pos]
        }.attach()

        binding.routeTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                currentFragment = p0?.position
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })

            Log.i("routeVPFrag", "routeID: $routeId")
            viewLifecycleOwner.lifecycleScope.launch {
                routeId?.let { viewModel.getRouteAndUpdateEditedRoute(it) }
                //isRouteLoaded = true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest {
                    initUi(it)
                }
            }
        }

        //Возврат
        binding.closeBtn.setOnClickListener {
            findNavController().popBackStack(R.id.routeListFragment,false)
        }
    }

    private fun stateListener(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    if (it == LoadState.Success.GoBack) findNavController().popBackStack(R.id.routeListFragment, false)
                }
            }
        }
    }

    private fun initUi(route: Route){
        val title = "Рейс № ${route.id}"
        //binding.titleRoute.text
        (activity as AppCompatActivity).supportActionBar?.title = title
        binding.driverTv.text = "${route.contractor?.driver?.surname},"
        //+" "+ route.driver?.firstName + " "+ route.driver?.middleName
        binding.truckTv.text = "${route.contractor?.truck?.regNumber} " +
                "${route.contractor?.truck?.regionCode},"
        binding.trailerTv.text = "${route.contractor?.trailer?.regNumber} " +
                "${route.contractor?.trailer?.regionCode}"
        binding.contractorValue.text = route.contractor?.company?.shortName
    }

    private fun  removeRoute(routeId: Long){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.removeRoute(routeId)
        }
    }

}