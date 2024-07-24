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
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.domain.models.Order
import ru.javacat.ui.adapters.OneLinePointAdapter
import ru.javacat.ui.databinding.FragmentOrderBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.utils.showOneInputDialog
import ru.javacat.ui.view_models.OrderViewModel

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderBinding
        get() = { inflater, container ->
            FragmentOrderBinding.inflate(inflater, container, false)
        }

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var pointsAdapter: OneLinePointAdapter

    private var orderIdArg: Long? = null
    private var routeId: Long? = null
    private var isNewOrder: Boolean? = false
    private var isPaid: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderIdArg = arguments?.getLong(FragConstants.ORDER_ID)

        isNewOrder = arguments?.getBoolean(FragConstants.IS_NEW_ORDER, false)

        setFragmentResultListener(FragConstants.NEW_VALUE) { _, bundle ->
            val docsNumber = bundle.getString(FragConstants.DOCS_NUMBER)
            docsNumber?.let {
                viewModel.updateOrderToDb(sentDocsNumber = it)
                binding.docsNumber.text = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_edit_remove, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        if (isNewOrder == true) {
                            findNavController().popBackStack(R.id.routeViewPagerFragment, false)
                        } else {
                            findNavController().navigateUp()
                        }
                        return true
                    }

                    R.id.edit_menu_item -> {
                        val bundle = Bundle()
                        bundle.putLong(FragConstants.ORDER_ID, orderIdArg ?: 0)
                        bundle.putLong(FragConstants.ROUTE_ID, routeId ?:0)
                        bundle.putBoolean(FragConstants.EDITING_ORDER, true)
                        findNavController().navigate(R.id.editOrderFragment, bundle)

                        return true
                    }

                    R.id.remove_menu_item -> {
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


        orderIdArg?.let { viewModel.getOrderById(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest {
                    //обновили текущий Рейс:
                    //it?.routeId?.let { it1 -> viewModel.updateEditedRoute(it1) }
                    //обновили экран:
                    it?.let { initUi(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                binding.progressBar.isVisible = it == LoadState.Loading
            }
        }

        binding.docsNumber.setOnClickListener {
            parentFragmentManager.showOneInputDialog(0, FragConstants.DOCS_NUMBER)
        }

        binding.docsReceivedDateTvValue.setOnClickListener {
            parentFragmentManager.showCalendar { date ->
                viewModel.updateOrderToDb(docsReceived = date)
                binding.docsReceivedDateTvValue.text = date.asDayAndMonthFully()
            }
        }

        binding.payDayValueTv.setOnClickListener {
            parentFragmentManager.showCalendar { date ->
                viewModel.updateOrderToDb(paymentDeadline = date)
                binding.payDayValueTv.text = date.asDayAndMonthFully()
            }
        }

        binding.paidBtn.setOnClickListener {
            if (isPaid) {
                viewModel.updateOrderToDb(isPaid = false)
                setUnpaidUi()
            } else {
                viewModel.updateOrderToDb(isPaid = true)
                setPaidUi()
            }
            isPaid = !isPaid
        }

    }

    private fun initUi(order: Order) {
        Log.i("OrderFrag", "order: $order")
        isPaid = order.isPaidByCustomer

        routeId = order.routeId

        val title = if (order.id == 0L) {
            "Новая заявка"
        } else {
            "Заявка № ${order.id}"
        }
        val typeOfUploadList = mutableListOf<String>()
        if (order.cargo?.isBackLoad == true) {
            typeOfUploadList.add("зад")
        }
        if (order.cargo?.isSideLoad == true) {
            typeOfUploadList.add("бок")
        }
        if (order.cargo?.isTopLoad == true) {
            typeOfUploadList.add("верх")
        }
        val typeOfUploadString = typeOfUploadList.joinToString(separator = "/")


        //TODO добавить в груз - способ погрузки - палеты, валом и тд
        //TODO добавить способ оплаты - нал безнал ндс

        (activity as AppCompatActivity).supportActionBar?.title = title

        binding.apply {
            callBtn.isGone = order.manager == null
            managerLabelTv.isGone = order.manager == null
            managerTv.isGone = order.manager == null

            customerTv.text = order.customer?.nameToShow
            managerTv.text = order.manager?.nameToShow
            cargoTv.text =
                "${order.cargo?.cargoName}, ${order.cargo?.cargoWeight} т / ${order.cargo?.cargoVolume} м3 "
            cargoExtraTv.text = typeOfUploadString
            priceTv.text = "${order.price} руб."
            priceExtraTv.text = "без НДС, ${order.daysToPay} б.дней"

            order.sentDocsNumber?.let {
                docsNumber.text = it
            }
            order.docsReceived?.let {
                docsReceivedDateTvValue.text = it.asDayAndMonthFully()
            }
            order.paymentDeadline?.let {
                payDayValueTv.text = it.asDayAndMonthFully()
            }

            if (order.isPaidByCustomer) {
                setPaidUi()
            } else setUnpaidUi()
        }

        //adapter
        pointsAdapter = OneLinePointAdapter()
        binding.pointsRecView.adapter = pointsAdapter
        pointsAdapter.submitList(order.points)
    }

    private fun setPaidUi() {
        binding.paidBtn.text = getString(R.string.mark_as_unpaid)
        binding.paidStatusTv.text = getString(R.string.paid_order)
    }

    private fun setUnpaidUi() {
        binding.paidBtn.text = getString(R.string.mark_as_paid)
        binding.paidStatusTv.text = getString(R.string.unpaid_order)
    }
}