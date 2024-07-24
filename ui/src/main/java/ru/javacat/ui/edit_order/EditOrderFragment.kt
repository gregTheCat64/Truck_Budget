package ru.javacat.ui.edit_order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.adapters.PointWithRemoveAdapter
import ru.javacat.ui.databinding.FragmentEditOrderBinding

import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.FragConstants.IS_NEW_ORDER
import ru.javacat.ui.view_models.EditOrderViewModel


@AndroidEntryPoint
class EditOrderFragment : BaseFragment<FragmentEditOrderBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentEditOrderBinding
        get() = { inflater, container ->
            FragmentEditOrderBinding.inflate(inflater, container, false)
        }

    private val viewModel: EditOrderViewModel by viewModels()
    private lateinit var pointsAdapter: PointWithRemoveAdapter


    private var currentOrder: Order? = null
    private var currentRoute: Route? = null
    private var isLastOrderLoaded = false

    //Переменные из EditText:
    private var newPrice: Int? = null
    private var newDaysToPay: Int? = null
    private var newContractorPrice: Int? = null

    private var newCargoWeight: Int? = null
    private var newCargoVolume: Int? = null

    private var isBackUpload: Boolean = false
    private var isSideUpload: Boolean = false
    private var isTopUpload: Boolean = false


    private var orderIdArg: Long? = null
    private var routeIdArg: Long? = null

    private var needToRestore: Boolean = false

    private val bundle = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("EditOrderFrag", "onCreate")

        orderIdArg = arguments?.getLong(FragConstants.ORDER_ID)
        routeIdArg = arguments?.getLong(FragConstants.ROUTE_ID)

        //если ордерайди не пришел, значит заказ - новый
        if (orderIdArg != 0L) needToRestore = true

        Log.i("EditOrderFrag", "orderId: $orderIdArg")
        Log.i("EditOrderFrag", "routeId: $routeIdArg")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i("EditOrderFrag", "onCreateView")
        Log.i("EditOrderFrag", "needToRestore>: $needToRestore")

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_save, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }

                    R.id.save -> {
                        saveOrder()
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

        if (!isLastOrderLoaded) {
            if (needToRestore) {
                Log.i("EditOrderFrag", "restoring Order")
                viewModel.updateEditedOrder(orderIdArg!!)
            } else {
                Log.i("EditOrderFrag", "creating empty Order")
                viewModel.createEmptyOrder()
                binding.tvVolume.setText("82")
                binding.weightTv.setText("20")
            }
            isLastOrderLoaded = true
        }


        //обновление  UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    if (order != null) {
                        Log.i("EditOrderFrag", "collecting Order: $order")
                        currentOrder = order
                        initUi(order)
                        paintOrder(R.drawable.unfilled_circle)
                        //в режиме редактирования восстанавливаем Рейс во флоу
                        if (needToRestore) {
                            Log.i(
                                "EditOrderFrag",
                                "restoringRoute by order.routeid, order.routeId: ${order.routeId}"
                            )
                            order.routeId.let { viewModel.updateEditedRoute(it) }
                            needToRestore = false
                        } else {
                            Log.i("EditOrderFrag", "restoringRoute by routeIdArg, order.routeId: $routeIdArg")
                            routeIdArg?.let { viewModel.updateEditedRoute(it) }
                        }
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest { route ->
                    Log.i("EditOrderFrag", "currentRoute: $route")
                    currentRoute = route

                    //если рейс привлеченный - поле с ценой для перевозчика видимо!
                    Log.i("EditOrderFrag", "order comp id: ${currentRoute?.contractor?.company?.id}")
                    binding.contractorsFrame.isGone =
                        currentRoute?.contractor?.company?.id == FragConstants.MY_COMPANY_ID
                }
            }
        }

        binding.addPointBtn.setOnClickListener {
            changingPoints()
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

        binding.backCheck.setOnCheckedChangeListener{_, isChecked->
                isBackUpload = isChecked
        }

        binding.sideCheck.setOnCheckedChangeListener{_, isChecked->
            isSideUpload = isChecked
        }

        binding.upCheck.setOnCheckedChangeListener{_, isChecked->
            isTopUpload = isChecked
        }

        addEditTextListeners()



        binding.paymentDeadlineChipGroup.setOnCheckedStateChangeListener { chipGroup, list ->
            val checkedId = chipGroup.checkedChipId
            //Log.i("AddpaymentFrag", "checkedId: $checkedId")
            val chip = chipGroup.findViewById<Chip>(checkedId)
            binding.daysToPayTv.setText(chip?.tag?.toString())
            //newDaysToPay = binding.daysToPayTv.text.toString().toInt()
            //viewModel.editOrder(daysToPay = chip?.tag?.toString()?.toInt())
        }

        binding.paymentTypeChipGroup.setOnCheckedStateChangeListener { chipGroup, list ->

        }

        //Сохранение заявки
        binding.saveBtn.setOnClickListener {
            saveOrder()
        }

        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                Log.i("EditOrderFrag", "state: $it")
                when {
                    it is LoadState.Success.GoBack -> {
                        //сохранились, идем назад:
                        if (needToRestore) {
                            Log.i("EditOrderFrag", "isEditing: $needToRestore")
                            findNavController().navigateUp()
                        } else findNavController().popBackStack(R.id.routeViewPagerFragment, false)
                    }

                    it is LoadState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.fill_requested_fields),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

        order.daysToPay?.let {
            val value = "$it"
            binding.daysToPayTv.setText(value)
        }

        order.price?.let {
            val value = "$it"
            binding.priceTv.setText(value)
        }

        order.cargo?.cargoVolume?.let {
            val value = "$it"
            binding.tvVolume.setText(value)
        }

        order.cargo?.cargoWeight?.let {
            val value = "$it"
            binding.weightTv.setText(value)
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



        order.contractorPrice?.let {
            val value = "$it"
            binding.contractorsPrice.setText(value)
        }

        binding.backCheck.isChecked = order.cargo?.isBackLoad == true
        binding.sideCheck.isChecked = order.cargo?.isSideLoad == true
        binding.upCheck.isChecked =  order.cargo?.isTopLoad == true

        //adapter
        pointsAdapter = PointWithRemoveAdapter {
            viewModel.removePoint(it)
        }
        binding.pointsRecView.adapter = pointsAdapter
        pointsAdapter.submitList(order.points)

    }

    private fun checkPayment(): Boolean {
        if (binding.priceTv.text.isNullOrEmpty() ||
            binding.daysToPayTv.text.isNullOrEmpty()
            ) {
            binding.paymentCheck.setImageDrawable(requireActivity().getDrawable(R.drawable.unfilled_circle))
            return false
        } else {
            newPrice = binding.priceTv.text.toString().toInt()
            newDaysToPay = binding.daysToPayTv.text.toString().toInt()
        }

        println("currentOrderIdComp = ${currentRoute?.contractor?.company?.id}")
        if (currentRoute?.contractor?.company?.id != FragConstants.MY_COMPANY_ID){
            if ( binding.contractorsPrice.text.isNullOrEmpty()){
                binding.paymentCheck.setImageDrawable(requireActivity().getDrawable(R.drawable.unfilled_circle))
                return false
            } else {
                newContractorPrice = binding.contractorsPrice.text?.toString()?.toInt()
            }
        }

        binding.paymentCheck.setImageDrawable(requireActivity().getDrawable(R.drawable.filled_circle))
        return true
    }

    private fun checkCargo(): Boolean {
        if (currentOrder?.cargo?.cargoName.isNullOrEmpty() ||
            binding.weightTv.text.isNullOrEmpty() ||
            binding.tvVolume.text.isNullOrEmpty()
            ) {
            binding.cargoCheck.setImageDrawable(requireActivity().getDrawable(R.drawable.unfilled_circle))
            return false
        } else {
            newCargoWeight = binding.weightTv.text.toString().toInt()
            newCargoVolume = binding.tvVolume.text.toString().toInt()
            binding.cargoCheck.setImageDrawable(requireActivity().getDrawable(R.drawable.filled_circle))
            return true
        }
    }

    private fun checkPoints(): Boolean {
        if ( currentOrder?.points?.size!! < 2){
            binding.routeCheck.setImageDrawable(requireActivity().getDrawable(R.drawable.unfilled_circle))
            return false
        } else {
            binding.routeCheck.setImageDrawable(requireActivity().getDrawable(R.drawable.filled_circle))
            return true
        }
    }

    private fun checkCustomer(): Boolean {
        return currentOrder?.customer != null
    }

//    private fun checkOrder(): Boolean {
//        if (checkCustomer() && checkPayment() && checkPoints() && checkCargo()) {
//            return true
//        } else {
//            paintOrder(R.drawable.unfilled_circle)
//            return false
//        }
//    }

    private fun paintOrder(unCheckedImage: Int) {
        val checkedCircle = requireContext().getDrawable(R.drawable.filled_circle)
        var unCheckedCircle = requireActivity().getDrawable(unCheckedImage)

        if (checkCustomer()) {
            binding.customerCheck.setImageDrawable(checkedCircle)
        } else {
            binding.scroll.smoothScrollTo(0, binding.customerCheck.baseline)
            binding.customerCheck.setImageDrawable(requireActivity().getDrawable(unCheckedImage))
        }

        if (checkCargo()) {
            binding.cargoCheck.setImageDrawable(checkedCircle)
        } else {
            binding.scroll.smoothScrollTo(0, binding.cargoCheck.baseline)
            binding.cargoCheck.setImageDrawable(unCheckedCircle)
        }

        if (checkPoints()) {
            binding.routeCheck.setImageDrawable(checkedCircle)
        } else {
            binding.scroll.smoothScrollTo(0, binding.routeCheck.baseline)
            binding.routeCheck.setImageDrawable(unCheckedCircle)
        }

        if (checkPayment()) {
            binding.paymentCheck.setImageDrawable(checkedCircle)
        } else {
            binding.scroll.smoothScrollTo(0, binding.paymentCheck.baseline)
            binding.paymentCheck.setImageDrawable(unCheckedCircle)
        }
    }

    private fun saveOrder() {
        val routeId = currentRoute?.id ?: 0
        val id = currentOrder?.id ?: 0

        val newContractor = currentOrder?.contractor ?: currentRoute?.contractor

        val newCargo = if (newCargoWeight != null && newCargoVolume != null) {
            currentOrder?.cargo?.copy(
                cargoWeight = newCargoWeight!!,
                cargoVolume = newCargoVolume!!,
                isBackLoad = isBackUpload,
                isSideLoad = isSideUpload,
                isTopLoad = isTopUpload
            )
        } else {
            currentOrder?.cargo
        }
        Log.i("EditOrderFrag", "newCargo: $newCargo")

        if (checkCustomer() && checkPoints() && checkCargo() && checkPayment()) {

            val newOrder = currentOrder?.copy(
                id,
                routeId = routeId,
                price = newPrice,
                daysToPay = newDaysToPay,
                contractor = newContractor,
                contractorPrice = newContractorPrice,
                cargo = newCargo,

            )

            if (newOrder != null) {
                viewModel.saveOrder(newOrder)
            }

        } else {
            paintOrder(R.drawable.error_circle)
            Toast.makeText(
                requireContext(),
                getString(R.string.fill_requested_fields),
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    private fun changingCustomer() {
        bundle.putBoolean(IS_NEW_ORDER, false)
        val dialogFragment = EditCustomerDialogFragment()
        dialogFragment.show(parentFragmentManager, "")
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
        findNavController().navigate(R.id.editPointsFragment)
    }

    private fun addEditTextListeners() {
        binding.weightTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                currentOrder?.let { checkCargo() }
                //checkOrder()
            }
        })

        binding.tvVolume.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {

                currentOrder?.let { checkCargo() }
            }
        })



        binding.priceTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                currentOrder?.let { checkPayment() }
            }
        })

        binding.contractorsPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                currentOrder?.let { checkPayment() }
            }
        })

        binding.daysToPayTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                currentOrder?.let { checkPayment() }
            }
        })
    }


}