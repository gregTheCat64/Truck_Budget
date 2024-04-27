package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.OrdersAdapter
import ru.javacat.ui.databinding.FragmentRouteBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.RouteViewModel

@AndroidEntryPoint
class RouteFragment : BaseFragment<FragmentRouteBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: RouteViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteBinding
        get() = { inflater, container ->
            FragmentRouteBinding.inflate(inflater, container, false)
        }

    private var currentRoute : Route? = null
    private var isRouteLoaded = false

    private lateinit var adapter: OrdersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = Bundle()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                when (it) {
                    LoadState.Loading -> binding.progressBar.isVisible = true
                    LoadState.Success.OK -> binding.progressBar.isGone = true
                    //LoadState.Success.GoForward -> findNavController().navigate(R.id.orderDetailsFragment)
                    //LoadState.Success.GoBack -> findNavController().navigate(R.id.routeListFragment)
                    else -> {}
                }
            }
        }

//        if (!isRouteLoaded && routeId != null) {
//            Log.i("routeFrag", "routeID: $routeId")
//            viewLifecycleOwner.lifecycleScope.launch {
//                viewModel.getRouteAndUpdateEditedRoute(routeId)
//                isRouteLoaded = true
//            }
//        }

        //Adapter
        adapter = OrdersAdapter{
            viewLifecycleOwner.lifecycleScope.launch {
                //viewModel.getOrderAndUpdateEditedOrder(it.id)
                bundle.putLong(FragConstants.ORDER_ID, it.id)
                bundle.putBoolean(FragConstants.EDITING_ORDER, true)
                //bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
                findNavController().navigate(R.id.orderDetailsFragment,bundle)
            }
        }
        binding.ordersList.adapter = adapter

        //инициализация UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest {
                    initUi(it)
                }
            }
        }

        //Новый заказ
        binding.addOrderBtn.setOnClickListener {
            viewModel.clearEditedOrder()
            viewModel.addRouteIdToOrder(routeId = currentRoute?.id?:0)
            findNavController().navigate(R.id.addCustomerFragment)
        }

        //Завершаем рейс
        binding.finishRouteBtn.setOnClickListener {
            calculate()
        }
    }

    private fun initUi(route: Route){
        currentRoute = route

        binding.docsImg.isGone = route.orderList.isNotEmpty()
        binding.addOrderBtn.isGone = route.isFinished
        binding.finishRouteBtn.isGone = route.isFinished || route.orderList.isEmpty()

        route.orderList.let {orders->
            adapter.submitList(orders)
        }
    }

    private fun calculate(){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            //viewModel.setRouteFinished()
            findNavController().navigate(R.id.finishRouteFragment)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

}