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
import ru.javacat.domain.models.ApiResult
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
import java.io.File

@AndroidEntryPoint
class RouteListFragment : BaseFragment<FragmentRouteListBinding>() {

    override var bottomNavViewVisibility: Int = View.VISIBLE

    private val viewModel: RouteListViewModel by viewModels()
    private lateinit var routesAdapter: RoutesAdapter
    //private var myCompany: Company? = null

    private var isFabVisible = true
    //private var routeId: Long? = null
    private val TAG = "RouteListFrag"

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
        val isAllowedToUpload = sharedPreferences.getBoolean("allowed", false)

        Log.i(TAG, "isModified: $isDbModified")
        Log.i(TAG, "isAuthorized: $isAuthorized")
        Log.i(TAG, "isAllowedToUpload: $isAllowedToUpload")

        Log.i(TAG, "onViewCreated")
        val currentYear = YearHolder.selectedYear

        binding.chooseYearBtn.text = currentYear.toString()
        updateList()

        binding.autoUpdateBtn.isChecked = isAllowedToUpload


        //если бд изменена, отправляем на яндекс
        if (isDbModified && isAuthorized && isAllowedToUpload){
            Log.i(TAG, "ismodified: $isDbModified isAuthorized: $isAuthorized")

            viewModel.uploadBdToYandexDisk(tokenValue!!){ result ->
                when (result) {
                    is ApiResult.Success -> Toast.makeText(requireContext(),
                        getString(R.string.uploaded_to_yandex_disk_successfully), Toast.LENGTH_SHORT).show()
                    is ApiResult.Error -> {
                        when (result) {
                            is ApiResult.Error.Unauthorized -> {
                                sharedPreferences.edit().putBoolean("authorized", false).apply()
                                Toast.makeText(requireContext(), "Not authorized", Toast.LENGTH_SHORT).show()
                            }
                            is ApiResult.Error.ServerError -> Toast.makeText(
                                requireContext(),
                                "Server Error",
                                Toast.LENGTH_SHORT
                            ).show()
                            is ApiResult.Error.InsufficientStorage -> Toast.makeText(
                                requireContext(),
                                "Not enough place at remote disk",
                                Toast.LENGTH_SHORT
                            ).show()
                            is ApiResult.Error.UnknownError -> Toast.makeText(
                                requireContext(),
                                "Error: ${result.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        //viewModel.getCustomerById(FragConstants.MY_COMPANY_ID)

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

        binding.autoUpdateBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Автоматическая синхронизация включена", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putBoolean("allowed", true).apply()
            } else {
                sharedPreferences.edit().putBoolean("allowed", false).apply()
                Toast.makeText(requireContext(), "Автоматическая синхронизация отключена", Toast.LENGTH_SHORT).show()
            }
        }

        binding.myTransportBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getCompanyRoutes(year = currentYear)
            } else viewModel.getAllRoutes(currentYear)
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