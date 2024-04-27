package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import ru.javacat.domain.models.Order
import ru.javacat.ui.adapters.OrdersAdapter
import ru.javacat.ui.databinding.FragmentOrderListBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.OrderListViewModel
import java.time.Month

@AndroidEntryPoint
class OrderListFragment: BaseFragment<FragmentOrderListBinding>() {

    override var bottomNavViewVisibility: Int = View.VISIBLE

    private val viewModel: OrderListViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderListBinding
        get() = {inflater, container ->
            FragmentOrderListBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Заявки"


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("orderListFrag", "onViewCreated")

        viewModel.getAllOrders()

        binding.checkbox.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) viewModel.getUnpaidOrders() else viewModel.getAllOrders()
        }

        binding.monthCheck.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) viewModel.getOrdersByMonth(Month.APRIL) else viewModel.getAllOrders()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                when (it) {
                    LoadState.Success.GoForward -> findNavController().navigate(R.id.orderDetailsFragment)
                    //LoadState.Success.GoBack -> findNavController().navigate(R.id.routeListFragment)
                    else -> {}
                }
            }
        }

        //Переделать в простую навигацию
        ordersAdapter = OrdersAdapter {
            viewLifecycleOwner.lifecycleScope.launch{
                //viewModel.getOrderAndUpdateEditedOrder(it.id)
                val bundle = Bundle()
                bundle.putLong(FragConstants.ORDER_ID, it.id)
                bundle.putBoolean(FragConstants.EDITING_ORDER, true)
                findNavController().navigate(R.id.orderDetailsFragment, bundle)
            }
        }

        binding.ordersRV.adapter = ordersAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.orders.collectLatest {
                    initUi(it)
                }
            }
        }
    }

    private fun initUi(orderList: List<Order?>){
        ordersAdapter.submitList(orderList)
    }
}