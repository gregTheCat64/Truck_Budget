package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
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

        val monthNames = arrayOf(
            getString(R.string.month),
            getString(R.string.january),
            getString(R.string.february),
            getString(R.string.march),
            getString(R.string.april),
            getString(R.string.may),
            getString(R.string.june),
            getString(R.string.july),
            getString(R.string.august),
            getString(R.string.september),
            getString(R.string.october),
            getString(R.string.november),
            getString(R.string.december))


        setFragmentResultListener(FragConstants.FILTER_ORDER){_, bundle ->
            val monthId = bundle.getInt(FragConstants.FILTER_MONTH, -1)
            val customerId = bundle.getLong(FragConstants.FILTER_CUSTOMER, -1L)
            val customerName = bundle.getString(FragConstants.CUSTOMER_NAME)

            Log.i("OrderListFrag", "monthId: $monthId")
            Log.i("OrderListFrag", "customerId: $customerId")

            if (monthId>=0){
                binding.monthsFilter.setText(monthNames.get(monthId))
                if (monthId > 0){
                    val monthToFilter = Month.of(monthId)
                    viewModel.setMonthFilter(monthToFilter)
                    binding.monthsFilter.isChecked = true
                } else {
                    viewModel.setMonthFilter(null)
                    binding.monthsFilter.isChecked = false
                }
            }


            if (customerId != -1L){
                if (customerId == 0L){
                    binding.companiesFilter.text = getString(R.string.customer)
                    viewModel.setCustomerFilter(null)
                    binding.companiesFilter.isChecked = false
                } else {
                    binding.companiesFilter.text = customerName
                    viewModel.setCustomerFilter(customerId)
                    binding.companiesFilter.isChecked = true
                }
            }
            viewModel.filterOrders()
        }

        viewModel.getAllOrders()

        binding.monthsFilter.setOnClickListener{
            val dialogFragment = MonthsDialogFragment()
            dialogFragment.show(parentFragmentManager, "")
        }

        binding.companiesFilter.setOnClickListener {
            val dialogFragment = CustomerDialogFragment()
            dialogFragment.show(parentFragmentManager, "")
        }


        binding.unpaidBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                viewModel.setPaidFilter(paidParam = false)
            } else {viewModel.setPaidFilter(paidParam = null)}

            viewModel.filterOrders()
        }

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

    private fun initUi(orderList: List<Order>){
        var debt = 0
        orderList.forEach {
            if (!it.isPaidByCustomer){
                debt += it.price
            }
        }
        binding.debtValue.text = "$debt руб."
        //Toast.makeText(requireContext(), "$debt", Toast.LENGTH_SHORT).show()
        ordersAdapter.submitList(orderList)
    }
}