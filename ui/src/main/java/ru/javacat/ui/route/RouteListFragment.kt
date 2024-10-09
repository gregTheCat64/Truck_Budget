package ru.javacat.ui.route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.YearHolder
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.adapters.OnRouteListener
import ru.javacat.ui.adapters.RoutesAdapter
import ru.javacat.ui.databinding.FragmentRouteListBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showYearCalendar

@AndroidEntryPoint
class RouteListFragment : BaseFragment<FragmentRouteListBinding>() {

    override var bottomNavViewVisibility: Int = View.VISIBLE

    private val viewModel: RouteListViewModel by viewModels()
    private lateinit var routesAdapter: RoutesAdapter
    //private var myCompany: Company? = null

    private var isFabVisible = true
    private var routeId: Long? = null

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

        val orderClickListener: (Order) -> Unit = {order ->
            val bundle = Bundle()
            bundle.putLong(FragConstants.ORDER_ID, order.id)
            //Toast.makeText(requireContext(), "Click", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_navigation_route_list_to_orderFragment, bundle)
        }

        //Adapter
        routesAdapter = RoutesAdapter(object : OnRouteListener {
            val bundle = Bundle()
            override fun onRoute(item: Route) {
                bundle.putLong(FragConstants.ROUTE_ID, item.id ?: 0L)
                findNavController().navigate(R.id.action_navigation_route_list_to_routeCountFragment, bundle)
            }

            override fun newOrder(item: Route) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.ROUTE_ID, item.id)
                findNavController().navigate(R.id.action_navigation_route_list_to_editOrderFragment, bundle)
            }


            override fun onRemove(item: Route) {
                viewLifecycleOwner.lifecycleScope.launch {
                    item.id?.let { viewModel.removeRoute(it) }
                }
            }
        }, orderClickListener)



        binding.routesList.adapter = routesAdapter

        binding.routesList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy>0 && isFabVisible) {
                    hideFab()
                } else if (dy<0 && !isFabVisible){
                    showFab()
                }
            }
        })

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
        findNavController().navigate(R.id.action_routeListFragment_to_newRouteFragment)

    }

    private fun hideFab() {
        binding.newRouteBtn.animate().translationY( binding.newRouteBtn.height.toFloat()+50).setDuration(300).start()
        isFabVisible = false
    }

    private fun showFab() {
        binding.newRouteBtn.animate().translationY(0f).setDuration(300).start()
        isFabVisible = true
    }

    private fun toFinishRouteFragment(currentRoute: Route){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            findNavController().navigate(R.id.finishRouteFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toFinishRouteWithOutDriverFragment(currentRoute: Route){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            findNavController().navigate(R.id.finishRouteWithoutDriverFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toFinishPartnerRouteFragment(currentRoute: Route){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            findNavController().navigate(R.id.finishPartnerRouteFragment, bundle)
        }else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }

    }
}