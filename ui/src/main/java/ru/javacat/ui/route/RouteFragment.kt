package ru.javacat.ui.route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.adapters.OrdersAdapter
import ru.javacat.ui.databinding.FragmentRouteBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class RouteFragment : BaseFragment<FragmentRouteBinding>() {

    private val viewModel: RouteViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteBinding
        get() = { inflater, container ->
            FragmentRouteBinding.inflate(inflater, container, false)
        }

    private var currentRoute : Route? = null
    private var isRouteLoaded = false

    private lateinit var adapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("RouteFrag", "onCreate")

        val args = arguments
        val routeId = args?.getLong(FragConstants.ROUTE_ID)

        Log.i("RouteFrag", "routeId: $routeId")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("RouteFrag", "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("RouteFrag", "onViewCreated")


        //Navigation
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


        //Adapter
        adapter = OrdersAdapter{
            viewLifecycleOwner.lifecycleScope.launch {
                //viewModel.getOrderAndUpdateEditedOrder(it.id)
                val bundle = Bundle()
                bundle.putLong(FragConstants.ORDER_ID, it.id)
                bundle.putLong(FragConstants.ROUTE_ID, it.routeId)
                //bundle.putBoolean(FragConstants.EDITING_ORDER, true)
                //bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
                findNavController().navigate(R.id.orderFragment,bundle)
            }
        }
        binding.ordersList.adapter = adapter

        //инициализация UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest {
                    if (it != null) {
                        initUi(it)
                    }
                }
            }
        }

        //Новый заказ
        binding.addOrderBtn.setOnClickListener {
            viewModel.clearOrder()
            //viewModel.addRouteIdToOrder(routeId = currentRoute?.id?:0)
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            //bundle.putBoolean(FragConstants.IS_NEW_ORDER, true)
            findNavController().navigate(R.id.editOrderFragment, bundle)
        }

        //Завершаем рейс
        binding.finishRouteBtn.setOnClickListener {
            if (currentRoute?.contractor?.company?.id == -1L){
                if (currentRoute?.contractor?.driver?.id == -1L){
                    toFinishRouteWithOutDriverFragment()
                } else {
                    toFinishRouteFragment()
                }
            } else toFinishPartnerRouteFragment()
        }
    }

    private fun initUi(route: Route){
        currentRoute = route

        binding.noRoutesLayout.isGone = route.orderList.isNotEmpty()
        binding.addOrderBtn.isGone = route.isFinished
        binding.finishRouteBtn.isGone = route.isFinished || route.orderList.isEmpty()

        route.orderList.let {orders->
            adapter.submitList(orders)
        }
    }

    private fun toFinishRouteFragment(){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            findNavController().navigate(R.id.finishRouteFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toFinishRouteWithOutDriverFragment(){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            findNavController().navigate(R.id.finishRouteWithoutDriverFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toFinishPartnerRouteFragment(){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            findNavController().navigate(R.id.finishPartnerRouteFragment, bundle)
        }else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }

    }


}