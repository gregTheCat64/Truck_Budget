package ru.javacat.ui.truck_fleet

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.adapters.RouteViewPagerAdapter
import ru.javacat.ui.databinding.FragmentTruckFleetViewPagerBinding
import ru.javacat.ui.utils.FragConstants

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

        (activity as AppCompatActivity).supportActionBar?.hide()


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
        companyId?.let { viewModel.setCurrentCompanyId(it) }

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

        binding.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
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