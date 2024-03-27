package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
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
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.OrderStatus
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.my_adapter.OneLinePointAdapter
import ru.javacat.ui.databinding.FragmentOrderDetailsBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.utils.showOneInputDialog
import ru.javacat.ui.view_models.OrderViewModel

const val IS_NEW_ORDER = "IS_NEW_ORDER"

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderDetailsBinding
        get() = { inflater, container ->
            FragmentOrderDetailsBinding.inflate(inflater, container, false)
        }

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var pointsAdapter: OneLinePointAdapter

    private var currentRoute = Route()
    private var currentOrder = Order(
        "", 0L, emptyList(), 0, Customer(null,""), Cargo(0,0,"",true, false,false),  null, null,
        null, null, null, false
    )

    private val bundle = Bundle()
    private var routeId = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(FragConstants.NEW_VALUE) {_, bundle ->
            val price = bundle.getString(FragConstants.PRICE)
            val daysToPay = bundle.getString(FragConstants.DAYS_TO_PAY)
            val docsNumber = bundle.getString(FragConstants.DOCS_NUMBER)

            if (price != null) viewModel.editOrder(price = price.toInt())
            if (daysToPay != null) viewModel.editOrder(daysToPay = daysToPay.toInt())
            if (docsNumber != null) viewModel.editOrder(sentDocsNumber = docsNumber)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Подписка на редактируемый Рейс
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                Log.i("OrderFrag", "editedRoute: $it")
                currentRoute = it
                routeId = it.id ?: 0L
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.isOrderEdited.collectLatest {
                        Log.i("orderFrag", " $it")
                        binding.saveBtn.isGone = !it
                    }
                }
        }

        initPointAdapter()
        initUi()
        paidCheck()
        //documentsCheckBoxListener()

        //viewModel.setOrderFlag(false)

        binding.customerTv.setOnClickListener {
            changingCustomer()
        }

        binding.editCargoBtn.setOnClickListener {
            changingCargo()
        }


        binding.editRouteBtn.setOnClickListener {
            changingPoints()
        }

        binding.editPaymentBtn.setOnClickListener {
            changingPayment()
        }

//        binding.priceTv.setOnClickListener {
//            parentFragmentManager.showOneInputDialog(currentOrder.price.toString(), FragConstants.PRICE)
//        }
//
//        binding.daysToPayTv.setOnClickListener {
//            parentFragmentManager.showOneInputDialog(currentOrder.daysToPay.toString(), FragConstants.DAYS_TO_PAY)
//        }

        binding.docsNumber.setOnClickListener {
            parentFragmentManager.showOneInputDialog(currentOrder.sentDocsNumber.toString(), FragConstants.DOCS_NUMBER)
        }

        binding.docsReceivedDateTvValue.setOnClickListener {
            parentFragmentManager.showCalendar { date->
                viewModel.editOrder(docsReceived = date)
            }
        }

        binding.payDayValueTv.setOnClickListener {
            parentFragmentManager.showCalendar { date->
                viewModel.editOrder(paymentDeadline = date)
            }
        }


        //Сохранение заявки
        binding.saveBtn.setOnClickListener {
            saveOrder()
        }

        //Отмена изменений
        binding.cancelBtn.setOnClickListener {
            viewModel.clearOrder()
            findNavController().navigate(R.id.routeFragment)
        }

        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it == LoadState.Success.GoBack) {
                    findNavController().navigate(R.id.routeFragment)
                }
            }
        }
    }

    private fun initUi(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    val title = if (order.id.isEmpty()) {
                        "Новая заявка"
                    } else {
                        "Заявка № ${order.id}"
                    }
                    binding.orderTitle.text = title
                    currentOrder = order

                    order.daysToPay?.let {
                        val value = "$it дней"
                        binding.daysToPayTv.text = value
                    }

                    order.price.let {
                        val value = "$it руб."
                        binding.priceTv.text = value
                    }

                    order.cargo.cargoVolume.let {
                        val value = "$it м3"
                        binding.tvVolume.text = value
                    }

                    order.cargo.cargoWeight.let {
                        val value = "$it т"
                        binding.weightTv.text = value
                    }

                    order.sentDocsNumber?.let {
                            binding.docsNumber.text = it
                    }
                    order.customer?.let {
                        binding.customerTv.text = it.name
                    }

                    order.cargo.cargoName?.let {
                        binding.cargoTv.text = it
                    }
                    order.docsReceived?.let {
                        binding.docsReceivedDateTvValue.text = it.asDayAndMonthFully()
                    }
                    order.paymentDeadline?.let {
                        binding.payDayValueTv.text = it.asDayAndMonthFully()
                    }

                    val loadList = StringBuilder()

                    if (order.cargo.isBackLoad) loadList.append("Зад")
                    if (order.cargo.isSideLoad) loadList.append("/Бок")
                    if (order.cargo.isTopLoad) loadList.append("/Верх")


                    binding.loadTypeTv.setText(loadList.toString())

                    pointsAdapter.submitList(order.points)

                    binding.payStatusCheck.isChecked = order.isPaid

//                    when (order.status) {
//                        OrderStatus.IN_PROGRESS -> binding.pending.isChecked = true
//                        OrderStatus.WAITING_FOR_PAYMENT -> {
//                            binding.finished.isChecked = true
//                            binding.payDayTv.isGone = false
//                            binding.payDayValueTv.isGone = false
//                        }
//                        OrderStatus.PAID -> {
//                            binding.paid.isChecked = true
//                            binding.payDayTv.isGone = true
//                            binding.payDayValueTv.isGone = true
//                        }
//                        else -> {}
//                    }
                }
            }
        }
    }

    private fun saveOrder() {
        val id =
            currentOrder.id.ifEmpty { "R$routeId" + "/" + (currentRoute.orderList.size + 1).toString() }

        val newOrder = viewModel.editedOrder.value.copy(
            id,
            routeId = routeId
        )
        viewModel.saveOrder(newOrder)
    }


    private fun initPointAdapter() {
        pointsAdapter = OneLinePointAdapter()
        binding.pointsRecView.adapter = pointsAdapter
    }

    private fun changingCustomer() {
        bundle.putBoolean(IS_NEW_ORDER, false)
        findNavController().navigate(R.id.addCustomerFragment, bundle)
    }

    private fun changingCargo() {
        bundle.putBoolean(IS_NEW_ORDER, false)
        findNavController().navigate(R.id.addCargoFragment, bundle)
    }

    private fun changingPoints() {
        bundle.putBoolean(IS_NEW_ORDER, false)
        findNavController().navigate(R.id.addPointsFragment, bundle)
    }

    private fun changingPayment() {
        bundle.putBoolean(IS_NEW_ORDER, false)
        findNavController().navigate(R.id.addPaymentFragment, bundle)
    }

    private fun paidCheck() {
        binding.payStatusCheck.setOnCheckedChangeListener { button, isChecked ->
            viewModel.editOrder(isPaid = isChecked)
        }
    }


//    private fun documentsCheckBoxListener() {
//        binding.statusRadioGroup.setOnCheckedChangeListener { _, i ->
//            when (i) {
//                R.id.pending -> {
//                    binding.documentsGroup.isGone = true
//                    viewModel.editOrder(status = OrderStatus.IN_PROGRESS)
//                }
//
//                R.id.finished -> {
//                    binding.documentsGroup.isGone = false
//                    viewModel.editOrder(status = OrderStatus.WAITING_FOR_PAYMENT)
//                    //binding.scroll.scrollToDescendant(binding.documentsGroup)
//                }
//
//                R.id.paid -> {
//                    binding.documentsGroup.isGone = false
//                    viewModel.editOrder(status = OrderStatus.PAID)
//                    //binding.scroll.scrollToDescendant(binding.documentsGroup)
//                }
//            }
//        }
//    }
}