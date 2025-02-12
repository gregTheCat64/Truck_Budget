package ru.javacat.ui.route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
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
    //private var routeId: Long? = null

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

        val sharedPreferences = activity?.getSharedPreferences("Prefs", MODE_PRIVATE)!!
        val isDbModified = sharedPreferences.getBoolean("db_modified", false)
        val tokenValue = sharedPreferences.getString("yandex_token", null)
        val isAuthorized = sharedPreferences.getBoolean("authorized", false)

        Log.i("routeListFrag", "onViewCreated")
        binding.chooseYearBtn.text = YearHolder.selectedYear.toString()
        updateList()

        //если бд изменена, отправляем на яндекс
        if (isDbModified && isAuthorized){
            viewModel.uploadBdToYandexDisk(tokenValue!!){
                    success ->
                if (success) {
                    Toast.makeText(requireContext(),
                        getString(R.string.uploaded_to_yandex_disk_successfully), Toast.LENGTH_SHORT).show()
                } else
                //вот тут сделать обработку, если токен истек!
                    Toast.makeText(requireContext(), "Failed to upload some files, try to repeat", Toast.LENGTH_SHORT).show()

            }
        }

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
            findNavController().navigate(R.id.action_navigation_route_list_to_orderFragment, bundle)
        }

        //Adapter
        routesAdapter = RoutesAdapter(object : OnRouteListener {
            val bundle = Bundle()
            override fun onRoute(item: Route) {
                bundle.putLong(FragConstants.ROUTE_ID, item.id)
                findNavController().navigate(R.id.action_navigation_route_list_to_routeCountFragment, bundle)
            }

            override fun newOrder(item: Route) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.ROUTE_ID, item.id)
                findNavController().navigate(R.id.action_navigation_route_list_to_editOrderFragment, bundle)
            }


            override fun onRemove(item: Route) {
                viewLifecycleOwner.lifecycleScope.launch {
                    item.id.let { viewModel.removeRoute(it) }
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

}