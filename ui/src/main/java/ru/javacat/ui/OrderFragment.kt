package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
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
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.OrderStatus
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.CustomersAdapter
import ru.javacat.ui.adapters.LocationAdapter
import ru.javacat.ui.adapters.OnPointListener
import ru.javacat.ui.adapters.PointsAdapter
import ru.javacat.ui.adapters.CargoAdapter
import ru.javacat.ui.adapters.my_adapter.OneLinePointAdapter
import ru.javacat.ui.databinding.FragmentOrderDetailsBinding
import ru.javacat.ui.utils.AndroidUtils
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.view_models.OrderViewModel

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderDetailsBinding
        get() = { inflater, container ->
            FragmentOrderDetailsBinding.inflate(inflater, container, false)
        }

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var pointsAdapter: OneLinePointAdapter

    private var currentRoute = Route()
    private var currentOrder = Order("", 0L, emptyList(), 0, null, 0L,
        0L, null, 0, 0, null, null, null,
        null, null, null, null)

    //private val itemParam = "item"
    //private val bundle = Bundle()
    private var routeId = 0L
    //private var locationsFound: Boolean = false
    //private var cargosFound: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("OrderFrag", "editedRoute: ${viewModel.editedRoute.value}")

        //Подписка на редактируемый Рейс
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                Log.i("OrderFrag", "editedRoute: ${it}")
                currentRoute = it
                routeId = it.id?:0L
            }
        }

        initPointAdapter()

        //инициализация UI
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
                    binding.daysToPayTv.text = order.daysToPay.toString() + " дней"

                    order.sentDocsNumber.let {
                        if (it != null){
                            binding.docsNumber.text = it
                        }
                    }

                    binding.customerTv.text = order.customer?.name
                    pointsAdapter.submitList(order.points)

                    order.cargoName.let {
                        if (it != null){
                            binding.cargoTv.setText(it)
                        }
                    }
                    binding.priceTv.text = order.price.toString() + " руб."

                    when (order.status){
                        OrderStatus.IN_PROGRESS -> binding.pending.isChecked = true
                        OrderStatus.WAITING_FOR_PAYMENT -> binding.finished.isChecked = true
                        OrderStatus.PAID -> {
                            binding.paid.isChecked = true
                            binding.payDayTv.isGone = true
                            binding.payDayValueTv.isGone = true
                        }

                        else -> {}
                    }
                }
            }
        }

        documentsCheckBoxListener()

        binding.editDocsBtn.setOnClickListener {
            findNavController().navigate(R.id.addDocumentsFragment)
        }

        //Сохранение заявки
        binding.saveBtn.setOnClickListener {
            val id =
                currentOrder.id.ifEmpty { "R$routeId" + "/" + (currentRoute.orderList.size + 1).toString() }

            val newOrder = viewModel.editedOrder.value.copy(
                id,
                routeId= routeId,
                driverId = currentRoute.driver?.id?:0,
                truckId = currentRoute.truck?.id?:0,
            )
            viewModel.saveOrder(newOrder)
        }

        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it == LoadState.Success.GoBack){
                    findNavController().navigate(R.id.routeFragment)
                }
            }
        }
    }


    private fun initPointAdapter() {
        pointsAdapter = OneLinePointAdapter()
        binding.recyclerView.adapter = pointsAdapter
    }


    private fun documentsCheckBoxListener() {
        binding.statusRadioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.pending -> {
                    binding.documentsGroup.isGone = true
                    viewModel.addStatusToOrder(OrderStatus.IN_PROGRESS)
                }

                R.id.finished -> {
                    binding.documentsGroup.isGone = false
                    viewModel.addStatusToOrder(OrderStatus.WAITING_FOR_PAYMENT)
                    binding.scroll.scrollToDescendant(binding.documentsGroup)
                }
                R.id.paid -> {
                    binding.documentsGroup.isGone = false
                    viewModel.addStatusToOrder(OrderStatus.PAID)
                    binding.scroll.scrollToDescendant(binding.documentsGroup)
                }
            }
        }
    }
}