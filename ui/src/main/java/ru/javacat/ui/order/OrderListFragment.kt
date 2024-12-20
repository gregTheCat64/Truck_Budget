package ru.javacat.ui.order

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
import androidx.core.view.isGone
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.FilterOrderModel
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.YearHolder
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.MonthsDialogFragment
import ru.javacat.ui.R
import ru.javacat.ui.adapters.OrdersAdapter
import ru.javacat.ui.companies.CustomerDialogFragment
import ru.javacat.ui.databinding.FragmentOrderListBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showYearCalendar
import java.time.Month
import kotlin.math.truncate


@AndroidEntryPoint
class OrderListFragment: BaseFragment<FragmentOrderListBinding>() {

    override var bottomNavViewVisibility: Int = View.VISIBLE

    private val viewModel: OrderListViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    private var isFabVisible = true

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderListBinding
        get() = {inflater, container ->
            FragmentOrderListBinding.inflate(inflater, container, false)
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
        Log.i("orderListFrag", "onViewCreated")

        val mAnimator = binding.ordersRV.itemAnimator as SimpleItemAnimator
        mAnimator.supportsChangeAnimations = true
        //binding.ordersRV.itemAnimator = DefaultItemAnimator().animateAppearance()

        setFragmentResultListener(FragConstants.FILTER_ORDER){_, bundle ->
            val monthId = bundle.getInt(FragConstants.FILTER_MONTH, -1)
            val customerId = bundle.getLong(FragConstants.FILTER_CUSTOMER, -1L)
            val customerName = bundle.getString(FragConstants.CUSTOMER_NAME)

            Log.i("OrderListFrag", "monthId: $monthId")
            Log.i("OrderListFrag", "customerId: $customerId")

            if (monthId>=0){

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
                    viewModel.setCustomerFilter(null, null)
                    binding.companiesFilter.isChecked = false
                } else {
                    viewModel.setCustomerFilter(customerId, customerName)
                    binding.companiesFilter.isChecked = true
                }
            }
            viewModel.filterOrders()
        }

        binding.chooseYearBtn.text = YearHolder.selectedYear.toString()
        binding.chooseYearBtn.setOnClickListener {
            showYearCalendar {
                    selectedYear ->
                YearHolder.selectedYear = selectedYear
                binding.chooseYearBtn.text = selectedYear.toString()
                updateList()
            }
        }

        updateList()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.filters.collectLatest {
                    updateFiltersUi(it)
                }
            }
        }

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
                viewModel.setPaidFilter(paidParam = true)
            } else {viewModel.setPaidFilter(paidParam = false)}

            viewModel.filterOrders()
        }

        ordersAdapter = OrdersAdapter {
            viewLifecycleOwner.lifecycleScope.launch{
                val bundle = Bundle()
                bundle.putLong(FragConstants.ORDER_ID, it.id)
                bundle.putLong(FragConstants.ROUTE_ID, it.routeId)
                bundle.putBoolean(FragConstants.IS_NEW_ORDER, false)
                findNavController().navigate(R.id.action_navigation_order_list_to_orderFragment, bundle)
            }
        }

        binding.ordersRV.adapter = ordersAdapter

        binding.ordersRV.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            private var previousScrollY = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy>0 && isFabVisible) {
                    hideFab()
                } else if (dy<0 && !isFabVisible){
                    showFab()
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.orders.collectLatest {
                    initUi(it)
                }
            }
        }
    }

    private fun initUi(orderList: List<Order>){
        binding.noOrdersLayout.isGone = orderList.isNotEmpty()
        var theirDebt = 0
        var myDebt = 0
        orderList.forEach {
            if (!it.isPaidByCustomer){
                theirDebt += it.price!!
            }
            if (!it.isPaidToContractor){
                myDebt += it.contractorPrice?:0
            }
        }
        if (theirDebt > 0) binding.theirDebtLayout.isGone = false
        binding.theirDebtValue.text = theirDebt.toString()

        if (myDebt > 0) binding.myDebtLayout.isGone = false
        binding.myDebtValue.text = myDebt.toString()

        ordersAdapter.submitList(orderList)
    }

    private fun updateFiltersUi(filterOrderModel: FilterOrderModel){
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

        binding.monthsFilter.setText(filterOrderModel.month?.value?.let { monthNames.get(it) }?:"Месяц")
        binding.companiesFilter.text = filterOrderModel.customerName?:"Клиент"
        //binding.unpaidBtn.isChecked = filterOrderModel.isUnPaid

    }

    private fun updateList(){
        val year = YearHolder.selectedYear
        viewModel.setYearFilter(year)
        viewModel.filterOrders()
    }

    private fun hideFab() {
        binding.debtsLayout.animate().translationY( binding.debtsLayout.height.toFloat()+42).setDuration(300).start()
        isFabVisible = false
    }

    private fun showFab() {
        binding.debtsLayout.animate().translationY(0f).setDuration(300).start()
        isFabVisible = true
    }
}