package ru.javacat.ui.route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
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
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.adapters.RouteViewPagerAdapter
import ru.javacat.ui.databinding.FragmentRouteViewPagerBinding

import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showDeleteConfirmationDialog

@AndroidEntryPoint
class RouteViewPagerFragment: BaseFragment<FragmentRouteViewPagerBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteViewPagerBinding
        get() = {inflater, container->
            FragmentRouteViewPagerBinding.inflate(inflater,container,false)
        }

    private val viewModel: RouteViewPagerViewModel by viewModels()

    var currentFragment: Int? = 0
    private var routeId: Long? = null
    val bundle = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("RouteVPFrag", "oncreate")

        routeId = arguments?.getLong(FragConstants.ROUTE_ID)

        Log.i("RouteVPfrag", "routeId: $routeId")

        bundle.putLong(FragConstants.ROUTE_ID, routeId?:0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("RouteVPFrag", "onCreateView")

        (activity as AppCompatActivity).supportActionBar?.hide()
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("RouteVPFrag", "onViewCreated")

        //viewModel.clearEditedOrder()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            findNavController().popBackStack(R.id.navigation_route_list, false)
        }

        val routeFragment = RouteFragment()
        routeFragment.arguments = bundle

        val routeCalculationInfoFragment =  RouteCalculationInfoFragment()
        routeFragment.arguments = bundle


        val fraglist = listOf(
            routeFragment,
            routeCalculationInfoFragment
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
                    it?.let { initUi(it) }
                }
            }
        }

        binding.actionBar.backBtn.setOnClickListener {
            findNavController().popBackStack(R.id.navigation_route_list,false)
        }

        binding.actionBar.moreBtn.setOnClickListener {
            showMenu(it)
        }
    }

    private fun stateListener(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    if (it == LoadState.Success.GoBack) findNavController().popBackStack(R.id.navigation_route_list, false)
                }
            }
        }
    }

    private fun initUi(route: Route){
        Log.i("RouteViewPagerFrag", "init route: $route")
        val title = "Рейс № ${route.id}"

        //(activity as AppCompatActivity).supportActionBar?.title = title
        binding.actionBar.title.text = title
        binding.actionBar.editBtn.isGone = true

        val truckInfoList = mutableListOf<String>()
        val truckDriver = "${route.contractor?.driver?.nameToShow}".trim()
        val truck = "${route.contractor?.truck?.regNumber} ${route.contractor?.truck?.regionCode?:""}".trim()
        val trailer = "${route.contractor?.trailer?.regNumber?:""} ${route.contractor?.trailer?.regionCode?:""}".trim()

        if (truckDriver.isNotEmpty()) truckInfoList.add(truckDriver)
        if (truck.isNotEmpty())truckInfoList.add(truck)
        if (trailer.isNotEmpty()) truckInfoList.add(trailer)

        val resultString = truckInfoList.joinToString(separator = ", ")

        binding.truckInfoTv.text = resultString
        binding.contractorValue.text = route.contractor?.company?.shortName
    }

    private fun  removeRoute(routeId: Long){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.removeRoute(routeId)
        }
    }

    private fun showMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.menu_remove)
        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item->
            when (item.itemId) {
                R.id.remove_menu_item -> {
                    showDeleteConfirmationDialog("рейс $routeId"){
                        routeId?.let { viewModel.removeRoute(it) }
                    }
                    routeId?.let { removeRoute(it) }
                }
                R.id.share_menu_item -> {
                    Toast.makeText(requireContext(), "Пока не реализовано", Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(context, "Пока не реализовано", Toast.LENGTH_SHORT).show()
            }
            true
        })
        menu.show()
    }
}