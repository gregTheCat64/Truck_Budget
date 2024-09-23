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
import ru.javacat.domain.models.PayType
import ru.javacat.domain.models.Route
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.adapters.PointWithRemoveAdapter
import ru.javacat.ui.databinding.FragmentEditOrderBinding

import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.FragConstants.IS_NEW_ORDER
import java.time.LocalDate


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
    private var newPayType: PayType? = null
    private var newDaysToPay: Int? = null
    private var newContractorPrice: Int? = null

    private var newDaysToPayToContractor: Int? = null
    private var newPayTypeToContractor: PayType? = null


    private var newCargoWeight: Int? = null
    private var newCargoVolume: Int? = null

    private var isBackUpload: Boolean = false
    private var isSideUpload: Boolean = false
    private var isTopUpload: Boolean = false

    private var startDate: LocalDate = LocalDate.now()

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

        (activity as AppCompatActivity).supportActionBar?.hide()
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val paddingToScroll = 200

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

        //adapter
        pointsAdapter = PointWithRemoveAdapter {
            viewModel.removePoint(it)
        }
        binding.pointsRecView.adapter = pointsAdapter



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

        binding.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.customerTv.setOnClickListener {
            changingCustomer()
        }

        binding.managerTv.setOnClickListener {
            if (currentOrder?.customer?.id != null){
            changingManager(currentOrder?.customer?.id!!)} else Toast.makeText(
                requireContext(),
                "Добавьте сначала заказчика",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.addPointBtn.setOnClickListener {
            binding.scroll.smoothScrollTo(0, binding.routeCheckTitle.top-paddingToScroll)
            changingPoints()
        }

        binding.cargoEditText.setOnClickListener {
            binding.scroll.smoothScrollTo(0, binding.cargoCheckLayout.top-paddingToScroll)
            changingCargo()
        }

        binding.tvVolume.setOnClickListener {
            binding.scroll.smoothScrollTo(0, binding.cargoCheckLayout.top-paddingToScroll)
            changingCargo()
        }

        binding.weightTv.setOnClickListener {
            binding.scroll.smoothScrollTo(0, binding.cargoCheckLayout.top-paddingToScroll)
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

        binding.priceTv.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                binding.scroll.post{
                    binding.scroll.smoothScrollTo(0, binding.paymentLayout.bottom-200)
                }
            }
        }

        binding.daysToPayTv.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                //binding.scroll.smoothScrollTo(0, -100)
            }
        }

        addEditTextListeners()


//        binding.paymentDeadlineChipGroup.setOnCheckedStateChangeListener { chipGroup, list ->
//            val checkedId = chipGroup.checkedChipId
//            //Log.i("AddpaymentFrag", "checkedId: $checkedId")
//            val chip = chipGroup.findViewById<Chip>(checkedId)
//            binding.daysToPayTv.setText(chip?.tag?.toString())
//            //newDaysToPay = binding.daysToPayTv.text.toString().toInt()
//            //viewModel.editOrder(daysToPay = chip?.tag?.toString()?.toInt())
//        }

        binding.paymentTypeChipGroup.setOnCheckedStateChangeListener { chipGroup, list ->
            val checkedId = chipGroup.checkedChipId
            val chip = chipGroup.findViewById<Chip>(checkedId)

            when (chip) {
                binding.card ->{
                    newPayType = PayType.CARD
                }
                binding.cash -> {
                    newPayType = PayType.CASH
                }
                binding.withFee -> {
                    newPayType = PayType.WITH_FEE
                }
                binding.withoutFee -> {
                    newPayType = PayType.WITHOUT_FEE
                }
            }
        }

        binding.paymentTypeChipToContractorGroup.setOnCheckedStateChangeListener { chipGroup, list ->
            val checkedId = chipGroup.checkedChipId
            val chip = chipGroup.findViewById<Chip>(checkedId)

            when (chip) {
                binding.cardToContractor ->{
                    newPayTypeToContractor = PayType.CARD
                }
                binding.cashToContractor -> {
                    newPayTypeToContractor = PayType.CASH
                }
                binding.withFeeToContractor -> {
                    newPayTypeToContractor = PayType.WITH_FEE
                }
                binding.withoutFeeToContractor -> {
                    newPayTypeToContractor = PayType.WITHOUT_FEE
                }
            }
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
                        Log.i("EditOrderFrag", "isNeedToRestore: $needToRestore")
                        if (orderIdArg!= 0L) {
                            Log.i("EditOrderFrag", "isEditing: $needToRestore")
                            findNavController().navigateUp()
                        } else {findNavController().popBackStack(R.id.routeViewPagerFragment, false)}
                        Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show()
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
            binding.cargoEditText.setText(it)
        }

        order.contractorPrice?.let {
            val value = "$it"
            binding.contractorsPrice.setText(value)
        }

        order.daysToPayToContractor?.let {
            val value = "$it"
            binding.daysToPaytoContractorEditText.setText(value)
        }

        when (order.payType) {
            PayType.CASH -> {
                binding.cash.isChecked = true
            }
            PayType.CARD -> {
                binding.card.isChecked = true
            }
            PayType.WITHOUT_FEE -> {
                binding.withoutFee.isChecked = true
            }
            PayType.WITH_FEE -> {
                binding.withFee.isChecked = true
            }
            else -> {

            }
        }

        when (order.payTypeToContractor) {
            PayType.CASH -> {
                binding.cashToContractor.isChecked = true
            }
            PayType.CARD -> {
                binding.cardToContractor.isChecked = true
            }
            PayType.WITHOUT_FEE -> {
                binding.withoutFeeToContractor.isChecked = true
            }
            PayType.WITH_FEE -> {
                binding.withFeeToContractor.isChecked = true
            }
            else -> {}
        }

        binding.backCheck.isChecked = order.cargo?.isBackLoad == true
        binding.sideCheck.isChecked = order.cargo?.isSideLoad == true
        binding.upCheck.isChecked =  order.cargo?.isTopLoad == true

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
            if ( binding.contractorsPrice.text.isNullOrEmpty() ||
                binding.daysToPaytoContractorEditText.text.isNullOrEmpty()
                ){
                binding.paymentCheck.setImageDrawable(requireActivity().getDrawable(R.drawable.unfilled_circle))
                return false
            } else {
                newContractorPrice = binding.contractorsPrice.text?.toString()?.toInt()
                newDaysToPayToContractor = binding.daysToPaytoContractorEditText.text.toString().toInt()
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
            startDate = currentOrder!!.points.first().arrivalDate
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
        val unCheckedCircle = requireActivity().getDrawable(unCheckedImage)

        if (checkCustomer()) {
            binding.customerCheck.setImageDrawable(checkedCircle)
        } else {
            binding.scroll.smoothScrollTo(0, binding.customerCheck.baseline)
            binding.customerCheck.setImageDrawable(unCheckedCircle)
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
                payType = newPayType?:PayType.WITHOUT_FEE,
                contractor = newContractor,
                contractorPrice = newContractorPrice,
                daysToPayToContractor = newDaysToPayToContractor,
                payTypeToContractor = newPayTypeToContractor,
                cargo = newCargo,
                date = startDate
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
        //findNavController().navigate(R.id.editPointsFragment)
        val dialogFragment = EditPointsFragment()
        dialogFragment.show(parentFragmentManager, "")
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