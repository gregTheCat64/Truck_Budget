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
import androidx.fragment.app.activityViewModels
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
import ru.javacat.domain.models.Company
import ru.javacat.ui.adapters.RouteViewPagerAdapter
import ru.javacat.ui.databinding.FragmentTruckFleetViewPagerBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.TruckFleetViewPagerViewModel

@AndroidEntryPoint
class TruckFleetViewPagerFragment: BaseFragment<FragmentTruckFleetViewPagerBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentTruckFleetViewPagerBinding
        get() = {inflater, container->
            FragmentTruckFleetViewPagerBinding.inflate(inflater,container,false)
        }

    private val viewModel: TruckFleetViewPagerViewModel by activityViewModels()

    var currentFragment: Int? = 0
    private var companyId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        companyId = arguments?.getLong(FragConstants.CUSTOMER_ID)
        Log.i("TruckFleetVP", "companyId: $companyId")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("TruckeFleetVPFragment", "onCreateView")

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_empty, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    else -> return false
                }
            }
        }, viewLifecycleOwner)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("TruckeFleetVPFragment", "onViewCreated")


        val fragList = listOf(
            TruckDriversListFragment(),
            TrucksListFragment(),
            TrailersListFragment()
        )


        val fragTitles = listOf(
            "TruckDrivers",
            "Trucks",
            "Trailers"
        )

        val adapter = RouteViewPagerAdapter(requireActivity(), fragList)
        binding.fleetViewPager.adapter = adapter

        TabLayoutMediator(binding.fleetTabLayout, binding.fleetViewPager){
            tab,pos -> tab.text = fragTitles[pos]
        }.attach()

        binding.fleetTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                currentFragment = p0?.position
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })


//        viewLifecycleOwner.lifecycleScope.launch {
//            companyId?.let { viewModel.updateCurrentCompany(it) }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                Log.i("DriverListFrag", "getting drivers list at companyId: $companyId")
                companyId?.let { viewModel.getDriverList(it) }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                Log.i("DriverListFrag", "getting trailer list at companyId: $companyId")
                companyId?.let { viewModel.getTrailersList(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                Log.i("DriverListFrag", "getting trucks list at companyId: $companyId")
                companyId?.let { viewModel.getTruckList(it) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("TruckeFleetVPFragment", "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("TruckeFleetVPFragment", "onDestroy")
    }
}