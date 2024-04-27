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
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.OneLinePointAdapter
import ru.javacat.ui.databinding.FragmentOrderDetailsBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.utils.showOneInputDialog
import ru.javacat.ui.view_models.OrderViewModel

const val IS_NEW_ORDER = "IS_NEW_ORDER"

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderDetailsBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderDetailsBinding
        get() = { inflater, container ->
            FragmentOrderDetailsBinding.inflate(inflater, container, false)
        }

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var pointsAdapter: OneLinePointAdapter

    private var currentOrder: Order? = null
    private var currentRoute: Route? = null

    //private var routeIdArg: Long? = null
    private var orderIdArg: Long? = null

    private var isEditingOrderArg: Boolean? = false

    private val bundle = Bundle()

    private var isOrderUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        isEditingOrderArg = arguments?.getBoolean(FragConstants.EDITING_ORDER, false)
        orderIdArg = arguments?.getLong(FragConstants.ORDER_ID)
        //routeId = arguments?.getLong(FragConstants.ROUTE_ID)

        Log.i("OrderFrag", "orderId: $orderIdArg")
        //Log.i("OrderFrag", "routeId: $routeId")

        setFragmentResultListener(FragConstants.NEW_VALUE) {_, bundle ->
            val price = bundle.getString(FragConstants.PRICE)
            val daysToPay = bundle.getString(FragConstants.DAYS_TO_PAY)
            val docsNumber = bundle.getString(FragConstants.DOCS_NUMBER)

            if (price != null) viewModel.editOrder(price = price.toInt())
            if (daysToPay != null) viewModel.editOrder(daysToPay = daysToPay.toInt())
            if (docsNumber != null) viewModel.editOrder(sentDocsNumber = docsNumber)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("OrderFrag", "isCreateOrderArg: $isEditingOrderArg")

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_edit, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
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

        //обнвляем Заявку если аргумент не тру и заявка еще не восстановлена(режим редактирования)
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                if (isEditingOrderArg == true && !isOrderUpdated) {
                    Log.i("OrderFrag", "restoring Order")
                    viewModel.restoringOrder(orderIdArg!!)
                    isOrderUpdated = true
                }
            }
        }

        //обновление  UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    if (order != null) {
                        initUi(order)
                        currentOrder = order
                        //в режиме редактирования восстанавливаем Рейс во флоу
                        if (isEditingOrderArg == true) {
                            viewModel.restoringRoute(order.routeId)
                        }
                    }
                    Log.i("OrderFrag", "currentOrder: $order")
                }
            }
        }

        //кнопка сохранить появляется если заявка изменена
        viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.isOrderEdited.collectLatest {
                        Log.i("orderFrag", " $it")
                        binding.saveBtn.isGone = !it
                    }
                }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest { route ->
                    Log.i("OrderFrag", "currentRoute: $route")
                    currentRoute = route
                }
            }
        }

        initPointAdapter()

        paidCheck()
        //documentsCheckBoxListener()

        //viewModel.setOrderFlag(false)

        binding.editCustomerTv.setOnClickListener {
            changingCustomer()
        }

        binding.editCargoTv.setOnClickListener {
            changingCargo()
        }


        binding.editPointsTv.setOnClickListener {
            changingPoints()
        }

        binding.editPaymentTv.setOnClickListener {
            //changingPayment()
        }

        binding.priceTv.setOnClickListener {
            parentFragmentManager.showOneInputDialog(currentOrder?.price.toString(), FragConstants.PRICE)
        }

        binding.daysToPayTv.setOnClickListener {
            parentFragmentManager.showOneInputDialog(
                currentOrder?.daysToPay?.toString() ?:"",
                FragConstants.DAYS_TO_PAY)
        }

        binding.docsNumber.setOnClickListener {
            parentFragmentManager.showOneInputDialog(currentOrder?.sentDocsNumber?:"", FragConstants.DOCS_NUMBER)
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
            //findNavController().navigateUp()
            if (isEditingOrderArg == true){
                findNavController().navigateUp()
            } else findNavController().popBackStack(R.id.viewPagerFragment, false)
        }

        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it == LoadState.Success.GoBack) {
                    //сохранились, идем назад:
                    if (isEditingOrderArg == true){
                        findNavController().navigateUp()
                    } else findNavController().popBackStack(R.id.viewPagerFragment, false)
                }
            }
        }
    }

    private fun initUi(order: Order){
            val title = if (order.id == 0L) {
                "Новая заявка"
            } else {
                "Заявка № ${order.id}"
            }
            //binding.orderTitle.text = title
            (activity as AppCompatActivity).supportActionBar?.title = title
            currentOrder = order

            order.daysToPay?.let {
                val value = "$it дней"
                binding.daysToPayTv.text = value
            }

            order.price.let {
                val value = "$it руб."
                binding.priceTv.text = value
            }

            order.cargo?.cargoVolume.let {
                val value = "$it м3"
                binding.tvVolume.text = value
            }

            order.cargo?.cargoWeight.let {
                val value = "$it т"
                binding.weightTv.text = value
            }

            order.sentDocsNumber?.let {
                binding.docsNumber.text = it
            }
            order.customer.let {
                binding.customerTv.text = it?.name
            }

            order.employee.let {
                binding.managerTv.text = it?.name
            }

            order.cargo?.cargoName.let {
                binding.cargoTv.text = it
            }
            order.docsReceived?.let {
                binding.docsReceivedDateTvValue.text = it.asDayAndMonthFully()
            }
            order.paymentDeadline?.let {
                binding.payDayValueTv.text = it.asDayAndMonthFully()
            }

            val loadList = StringBuilder()

            if (order.cargo?.isBackLoad == true) loadList.append("Зад")
            if (order.cargo?.isSideLoad == true) loadList.append("/Бок")
            if (order.cargo?.isTopLoad == true) loadList.append("/Верх")

            binding.loadTypeTv.setText(loadList.toString())

            pointsAdapter.submitList(order.points)

            binding.payStatusCheck.isChecked = order.isPaid

    }

    private fun saveOrder() {
        val routeId = currentRoute?.id?:0

        val id = currentOrder?.id?:0
            //currentOrder.id.ifEmpty { "R$routeId" + "/" + (currentRoute.orderList.size + 1).toString() }

        val routeTruck = currentOrder?.truck?:currentRoute?.truck

        val routeTrailer = currentOrder?.trailer?:currentRoute?.trailer

        val routeDriver = currentOrder?.driver?:currentRoute?.driver


        val newOrder = viewModel.editedOrder.value?.copy(
            id,
            routeId = routeId,
            driver = routeDriver,
            truck = routeTruck,
            trailer = routeTrailer
        )
        if (newOrder != null) {
            viewModel.saveOrder(newOrder)
        }
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
        binding.payStatusCheck.setOnCheckedChangeListener { _, isChecked ->
            viewModel.editOrder(isPaid = isChecked)
        }
    }



}