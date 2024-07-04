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
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.OneLinePointAdapter
import ru.javacat.ui.databinding.FragmentEditOrderBinding
import ru.javacat.ui.edit_order.EditCargoDialogFragment
import ru.javacat.ui.edit_order.EditCustomerDialogFragment
import ru.javacat.ui.edit_order.EditManagerDialogFragment

import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.FragConstants.IS_NEW_ORDER
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.utils.showOneInputDialog
import ru.javacat.ui.view_models.EditOrderViewModel


@AndroidEntryPoint
class EditOrderFragment : BaseFragment<FragmentEditOrderBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentEditOrderBinding
        get() = { inflater, container ->
            FragmentEditOrderBinding.inflate(inflater, container, false)
        }

    private val viewModel: EditOrderViewModel by viewModels()
    private lateinit var pointsAdapter: OneLinePointAdapter

    private var currentOrder: Order? = null
    private var currentRoute: Route? = null

    var customerId: Long? = null
    var customerName: String? = null

    private var orderIdArg: Long? = null

    private var isEditingOrderArg: Boolean? = false

    private val bundle = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isEditingOrderArg = arguments?.getBoolean(FragConstants.EDITING_ORDER, false)
        orderIdArg = arguments?.getLong(FragConstants.ORDER_ID)
        //routeId = arguments?.getLong(FragConstants.ROUTE_ID)

        Log.i("OrderFrag", "orderId: $orderIdArg")
        //Log.i("OrderFrag", "routeId: $routeId")

        setFragmentResultListener(FragConstants.NEW_VALUE) { _, bundle ->

            val price = bundle.getString(FragConstants.PRICE)
            val daysToPay = bundle.getString(FragConstants.DAYS_TO_PAY)
            val docsNumber = bundle.getString(FragConstants.DOCS_NUMBER)
            val cargoWeight = bundle.getString(FragConstants.CARGO_WEIGHT)
            val cargoVolume = bundle.getString(FragConstants.CARGO_VOLUME)

            if (price != null) viewModel.editOrder(price = price.toInt())
            if (daysToPay != null) viewModel.editOrder(daysToPay = daysToPay.toInt())
            if (docsNumber != null) viewModel.editOrder(sentDocsNumber = docsNumber)

            cargoWeight?.let { viewModel.editOrder(
                cargo = currentOrder?.cargo?.copy(
                    cargoWeight = it.toInt()
                )
            ) }

            cargoVolume?.let { viewModel.editOrder(
                cargo = currentOrder?.cargo?.copy(
                    cargoVolume = it.toInt()
                )
            ) }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("OrderFrag", "isCreateOrderArg: $isEditingOrderArg")

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_cancel, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }

                    R.id.cancel_button_menu_item -> {
                        viewModel.clearOrder()
                        //findNavController().navigateUp()
                        if (isEditingOrderArg == true) {
                            findNavController().navigateUp()
                        } else findNavController().popBackStack(R.id.viewPagerFragment, false)
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


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //if (isEditingOrderArg == true && !isOrderUpdated) {
                Log.i("OrderFrag", "restoring Order")
                viewModel.restoringOrder(orderIdArg!!)
                // isOrderUpdated = true
            }
        }

        //обновление  UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    initUi(order)
                    currentOrder = order
                    //в режиме редактирования восстанавливаем Рейс во флоу
                    if (isEditingOrderArg == true) {
                        viewModel.restoringRoute(order.routeId)
                    }
                    Log.i("OrderFrag", "currentOrder: $order")
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



        binding.customerTv.setOnClickListener {
            changingCustomer()
        }

        binding.managerTv.setOnClickListener {
            currentOrder?.customer?.id?.let { it1 -> changingManager(it1) }
        }

        binding.cargoTv.setOnClickListener {
            changingCargo()
        }

        binding.weightTv.setOnClickListener {
            parentFragmentManager.showOneInputDialog(
                currentOrder?.cargo?.cargoWeight.toString(),
                FragConstants.CARGO_WEIGHT
            )
        }

        binding.tvVolume.setOnClickListener {
            parentFragmentManager.showOneInputDialog(
                currentOrder?.cargo?.cargoVolume.toString(),
                FragConstants.CARGO_VOLUME
            )
        }

        binding.editPointsTv.setOnClickListener {
            changingPoints()
        }

        binding.editPaymentTv.setOnClickListener {
            //changingPayment()
        }

        binding.priceTv.setOnClickListener {
            parentFragmentManager.showOneInputDialog(
                currentOrder?.price.toString(),
                FragConstants.PRICE
            )
        }

        binding.daysToPayTv.setOnClickListener {
            parentFragmentManager.showOneInputDialog(
                currentOrder?.daysToPay?.toString() ?: "",
                FragConstants.DAYS_TO_PAY
            )
        }

        binding.docsNumber.setOnClickListener {
            parentFragmentManager.showOneInputDialog(
                currentOrder?.sentDocsNumber ?: "",
                FragConstants.DOCS_NUMBER
            )
        }

        binding.docsReceivedDateTvValue.setOnClickListener {
            parentFragmentManager.showCalendar { date ->
                viewModel.editOrder(docsReceived = date)
            }
        }

        binding.payDayValueTv.setOnClickListener {
            parentFragmentManager.showCalendar { date ->
                viewModel.editOrder(paymentDeadline = date)
            }
        }


        //Сохранение заявки
        binding.saveBtn.setOnClickListener {
            saveOrder()
        }


        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                Log.i("OrderFrag", "state: $it")
                if (it == LoadState.Success.GoBack) {
                    //сохранились, идем назад:
                    if (isEditingOrderArg == true) {
                        Log.i("OrderFrag", "isEditing: $isEditingOrderArg")
                        findNavController().navigateUp()
                    } else findNavController().popBackStack(R.id.viewPagerFragment, false)
                }
            }
        }
    }

    private fun initUi(order: Order) {
        val title = if (order.id == 0L) {
            "Новая заявка"
        } else {
            "Заявка № ${order.id}"
        }
        //binding.orderTitle.text = title
        (activity as AppCompatActivity).supportActionBar?.title = title
        currentOrder = order

        paidCheck()

        order.daysToPay?.let {
            val value = "$it"
            binding.daysToPayTv.setText(value)
        }

        order.price.let {
            val value = "$it"
            binding.priceTv.setText(value)
        }

        order.cargo?.cargoVolume.let {
            val value = "$it"
            binding.tvVolume.setText(value)
        }

        order.cargo?.cargoWeight.let {
            val value = "$it"
            binding.weightTv.setText(value)
        }

        order.sentDocsNumber?.let {
            binding.docsNumber.setText(it)
        }
        order.customer.let {
            binding.customerTv.setText(it?.nameToShow)
        }

        order.manager.let {
            binding.managerTv.setText(it?.nameToShow)
        }

        order.cargo?.cargoName.let {
            binding.cargoTv.setText(it)
        }
        order.docsReceived?.let {
            binding.docsReceivedDateTvValue.setText(it.asDayAndMonthFully())
        }
        order.paymentDeadline?.let {
            binding.payDayValueTv.setText(it.asDayAndMonthFully())
        }

        if (order.contractorPrice != null) {
            binding.contractorsPriceLayout.isVisible = true
            order.contractorPrice?.let {
                val value = "$it руб."
                binding.contractorsPrice.setText(value)
            }
        }

        if (order.cargo?.isBackLoad == true) binding.backCheck.isChecked = true
        if (order.cargo?.isSideLoad == true) binding.sideCheck.isChecked = true
        if (order.cargo?.isTopLoad == true) binding.upCheck.isChecked = true

        binding.paidBtn.isChecked = order.isPaidByCustomer
        binding.paidBtn.text = if (order.isPaidByCustomer) {
            getString(R.string.paid_order)
        } else getString(R.string.unpaid_order)

        //adapter
        pointsAdapter = OneLinePointAdapter()
        binding.pointsRecView.adapter = pointsAdapter
        pointsAdapter.submitList(order.points)

    }

    private fun saveOrder() {
        val routeId = currentRoute?.id ?: 0

        val id = currentOrder?.id ?: 0
        //currentOrder.id.ifEmpty { "R$routeId" + "/" + (currentRoute.orderList.size + 1).toString() }

        val routeTruck = currentOrder?.truck ?: currentRoute?.contractor?.truck

        val routeTrailer = currentOrder?.trailer ?: currentRoute?.contractor?.trailer

        val routeDriver = currentOrder?.driver ?: currentRoute?.contractor?.driver

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


    private fun changingCustomer() {
        bundle.putBoolean(IS_NEW_ORDER, false)
        val dialogFragment = EditCustomerDialogFragment()
        dialogFragment.show(parentFragmentManager, "")
        //findNavController().navigate(R.id.addCustomerFragment, bundle)
    }

    private fun changingManager(customerId: Long) {
        Log.i("EditOrderFragment", "customerId: $customerId")
        bundle.putLong(FragConstants.COMPANY_ID, customerId)
        val dialogFragment = EditManagerDialogFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(parentFragmentManager, "")
    }

    private fun changingCargo() {
        val dialogFragment = EditCargoDialogFragment()
        dialogFragment.show(parentFragmentManager, "")
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
        binding.paidBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.editOrder(isPaid = isChecked)
            binding.paidBtn.text =
                if (isChecked) getString(R.string.paid_order) else getString(R.string.unpaid_order)
        }

//        binding.paidBtn.setOnClickListener {
//            if (it.isActivated){
//                viewModel.editOrder(isPaid = true)
//                binding.paidBtn.text = getString(R.string.paid_order)
//            } else {
//                viewModel.editOrder(isPaid = false)
//                binding.paidBtn.text = getString(R.string.unpaid_order)
//            }
//            //viewModel.editOrder(isPaid = !currentOrder?.isPaidByCustomer!!)
//        }
    }


}