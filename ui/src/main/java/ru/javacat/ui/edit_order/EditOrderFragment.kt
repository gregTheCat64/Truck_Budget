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


    var customerId: Long? = null

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

        setFragmentResultListener(FragConstants.NEW_VALUE) { _, bundle ->
            //val price = bundle.getInt(FragConstants.PRICE)
            val contractorsPrice = bundle.getInt(FragConstants.PRICE)
            val daysToPay = bundle.getInt(FragConstants.DAYS_TO_PAY)
            val docsNumber = bundle.getInt(FragConstants.DOCS_NUMBER)

            val cargoWeight = bundle.getInt(FragConstants.CARGO_WEIGHT)
            val cargoVolume = bundle.getInt(FragConstants.CARGO_VOLUME)

            //if (price != null) viewModel.editOrder(price = price.toInt())
            if (contractorsPrice != null) viewModel.editOrder(contractorsPrice=contractorsPrice.toInt())
            if (daysToPay != null) viewModel.editOrder(daysToPay = daysToPay.toInt())
            if (docsNumber != null) viewModel.editOrder(sentDocsNumber = docsNumber.toString())

            cargoWeight?.let {
                viewModel.editOrder(
                    cargo = currentOrder?.cargo?.copy(
                        cargoWeight = it.toInt()
                    )
                )
            }

            cargoVolume?.let {
                viewModel.editOrder(
                    cargo = currentOrder?.cargo?.copy(
                        cargoVolume = it.toInt()
                    )
                )
            }
        }
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
                menuInflater.inflate(R.menu.menu_empty, menu)
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

        if (!isLastOrderLoaded){
            if (needToRestore) {
                Log.i("EditOrderFrag", "restoring Order")
                viewModel.updateEditedOrder(orderIdArg!!)
            } else {
                Log.i("EditOrderFrag", "creating empty Order")
                viewModel.createEmptyOrder()
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
                                "restoringRoute, order.routeId: ${order.routeId}"
                            )
                            order.routeId.let { viewModel.updateEditedRoute(it) }
                            needToRestore = false
                        } else {
                            Log.i("EditOrderFrag", "restoringRoute, order.routeId: $routeIdArg")
                            routeIdArg?.let { viewModel.updateEditedRoute(it) }
                        }
                    }
                    Log.i("EditOrderFrag", "currentOrder: $order")
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest { route ->
                    Log.i("EditOrderFrag", "currentRoute: $route")
                    currentRoute = route
                    binding.contractorsFrame.isGone = currentRoute?.contractor?.company?.id == FragConstants.MY_COMPANY_ID
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

        addEditTextListeners()



        binding.paymentDeadlineChipGroup.setOnCheckedStateChangeListener { chipGroup, list ->
            val checkedId = chipGroup.checkedChipId
            //Log.i("AddpaymentFrag", "checkedId: $checkedId")
            val chip = chipGroup.findViewById<Chip>(checkedId)
            binding.daysToPayTv.setText(chip?.tag?.toString())
            viewModel.editOrder(daysToPay = chip?.tag?.toString()?.toInt())
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

        order.cargo?.cargoVolume.let {
            val value = "$it"
            binding.tvVolume.setText(value)
        }

        order.cargo?.cargoWeight.let {
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

        Log.i("EditOrderFrag", "order comp id: ${currentRoute?.contractor?.company?.id}")
        binding.contractorsFrame.isGone = currentRoute?.contractor?.company?.id == FragConstants.MY_COMPANY_ID

        order.contractorPrice?.let {
            val value = "$it руб."
            binding.contractorsPrice.setText(value)
        }

        if (order.cargo?.isBackLoad == true) binding.backCheck.isChecked = true
        if (order.cargo?.isSideLoad == true) binding.sideCheck.isChecked = true
        if (order.cargo?.isTopLoad == true) binding.upCheck.isChecked = true

        //adapter
        pointsAdapter = PointWithRemoveAdapter {
            viewModel.removePoint(it)
        }
        binding.pointsRecView.adapter = pointsAdapter
        pointsAdapter.submitList(order.points)

    }

    private fun checkPayment(): Boolean{
        if (binding.priceTv.text.isNullOrEmpty()) {return false} else {
            newPrice = binding.priceTv.text.toString().toInt()
        }
        if (binding.daysToPayTv.text.isNullOrEmpty()) {return false} else {
            newDaysToPay = binding.daysToPayTv.text.toString().toInt()
        }
        if (currentOrder?.contractor?.company?.id != FragConstants.MY_COMPANY_ID
            && binding.contractorsPrice.text.isNullOrEmpty()
            ) {return false} else {
                newContractorPrice = binding.contractorsPrice.text?.toString()?.toInt()
        }

        return true
    }

    private fun checkCargo(): Boolean{
        if (currentOrder?.cargo?.cargoName.isNullOrEmpty()) {return false}
        if (binding.weightTv.text.isNullOrEmpty()) {return false} else {
            newCargoWeight = binding.weightTv.text.toString().toInt()
        }
        if (binding.tvVolume.text.isNullOrEmpty()) {return false} else {
            newCargoVolume = binding.tvVolume.text.toString().toInt()
        }

        return true
    }

    private fun checkPoints(): Boolean{
         return currentOrder?.points?.size!! >= 2
    }

    private fun checkCustomer(): Boolean{
        return currentOrder?.customer != null
    }

    private fun paintOrder(unCheckedImage: Int){
        val checkedCircle = requireContext().getDrawable(R.drawable.filled_circle)
        var unCheckedCircle = requireActivity().getDrawable(unCheckedImage)

        if (checkCustomer()) {
            binding.customerCheck.setImageDrawable(checkedCircle)
        } else  binding.customerCheck.setImageDrawable(unCheckedCircle)

        if (checkCargo()) {
            binding.cargoCheck.setImageDrawable(checkedCircle)
        } else  binding.cargoCheck.setImageDrawable(unCheckedCircle)

        if (checkPoints()){
            binding.routeCheck.setImageDrawable(checkedCircle)
        } else binding.routeCheck.setImageDrawable(unCheckedCircle)

        if (checkPayment()){
            binding.paymentCheck.setImageDrawable(checkedCircle)
        } else binding.paymentCheck.setImageDrawable(unCheckedCircle)
    }

    private fun saveOrder() {
        val routeId = currentRoute?.id ?: 0
        val id = currentOrder?.id ?: 0


        val contractor = currentOrder?.contractor?: currentRoute?.contractor


        val newCargo = if (newCargoWeight != null && newCargoVolume != null) {
            viewModel.editedOrder.value?.cargo?.copy(
                cargoWeight = newCargoWeight!!,
                cargoVolume = newCargoVolume!!
            )
        } else {
            null
        }

        //Добавить проверку всё ли ок и чек
        if (checkCustomer() && checkPoints() && checkCustomer() && checkPayment()){

            val newOrder = viewModel.editedOrder.value?.copy(
                id,
                routeId = routeId,
                contractor = contractor,
                price = newPrice,
                daysToPay = newDaysToPay,
                contractorPrice = newContractorPrice,
                cargo = newCargo
            )

            if (newOrder != null) {
                viewModel.saveOrder(newOrder)
            }
        } else {

            //unCheckedCircle = requireActivity().getDrawable(R.drawable.baseline_cancel_24)

            paintOrder(R.drawable.baseline_cancel_24)
            Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
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

    private fun addEditTextListeners(){
        binding.weightTv.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                currentOrder?.let { checkCargo() }
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