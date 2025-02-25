package ru.javacat.ui.order

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
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
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.domain.models.Order
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.adapters.OneLinePointAdapter
import ru.javacat.ui.databinding.FragmentOrderBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.utils.showOneInputDialog
import java.time.LocalDate
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.PopupMenu
import android.widget.TextView
import ru.javacat.common.utils.asDayAndMonthShortly
import ru.javacat.common.utils.toPrettyNumber
import ru.javacat.common.utils.toPrettyPrice
import ru.javacat.ui.OneInputValueDialogFragment
import ru.javacat.ui.utils.makePhoneCall
import ru.javacat.ui.utils.sendMessageToWhatsApp
import ru.javacat.ui.utils.shareMessage
import ru.javacat.ui.utils.showDeleteConfirmationDialog

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
    private var currentOrder: Order? = null

    private var isPaid: Boolean = false
    private var isPaidToContractor: Boolean = false
    private var sentDocsNumber: String? = null
    private var paymentDeadline: LocalDate? = null
    private var docsReceived: LocalDate? = null

    private var companyPhoneNumber: String? = null
    private var managerPhoneNumber: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderIdArg = arguments?.getLong(FragConstants.ORDER_ID)

        isNewOrder = arguments?.getBoolean(FragConstants.IS_NEW_ORDER, false)

        setFragmentResultListener(FragConstants.NEW_VALUE) { _, bundle ->
            val docsNumber = bundle.getString(FragConstants.DOCS_NUMBER)
            docsNumber?.let {
                //println("НОМЕР ПРИШЕЛ: $docsNumber")
                sentDocsNumber = it
                updateOrderToDb()
                binding.docsNumber.text = it
                if (it.isNotEmpty()){
                    binding.copyBtn.isGone = false
                } else binding.docsNumber.text = ""
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderIdArg?.let { viewModel.getOrderById(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest {
                    currentOrder = it
                    //обновили текущий Рейс:
                    //it?.routeId?.let { it1 -> viewModel.updateEditedRoute(it1) }
                    //обновили экран:
                    it?.let { initUi(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    binding.progressBar.isVisible = it == LoadState.Loading
                    if (it is LoadState.Success.Removed){
                        findNavController().navigateUp()
                    }
                }
            }

        }

        binding.actionBar.backBtn.setOnClickListener {
            if (isNewOrder == true) {
                findNavController().popBackStack(R.id.routeViewPagerFragment, false)
            } else {
            findNavController().navigateUp()
        }
        }

        binding.actionBar.editBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(FragConstants.ORDER_ID, orderIdArg ?: 0)
            bundle.putLong(FragConstants.ROUTE_ID, routeId ?:0)
            bundle.putBoolean(FragConstants.EDITING_ORDER, true)

            findNavController().navigate(R.id.action_orderFragment_to_editOrderFragment, bundle)
        }

        binding.actionBar.moreBtn.setOnClickListener {
            showMenu(it)
        }

        binding.customerTv.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(FragConstants.CUSTOMER_ID, currentOrder?.customer?.id?:0L)
            findNavController().navigate(R.id.action_orderFragment_to_companyFragment, bundle)
        }

        binding.docsNumber.setOnClickListener {
            val oldValue = sentDocsNumber
            //parentFragmentManager.showOneInputDialog(oldValue, FragConstants.DOCS_NUMBER)
            val bundle = Bundle()
            bundle.putString(FragConstants.DOCS_NUMBER, oldValue)
            val dialogFragment = OneInputValueDialogFragment()
            dialogFragment.arguments = bundle
            dialogFragment.show(parentFragmentManager, "")
        }

        binding.docsReceivedDateTvValue.setOnClickListener {
            parentFragmentManager.showCalendar { date ->
                //viewModel.updateOrderToDb(docsReceived = date)
                docsReceived = date
                binding.docsReceivedDateTvValue.text = date.asDayAndMonthFully()
                updateOrderToDb()
            }
        }

        binding.payDayValueTv.setOnClickListener {
            parentFragmentManager.showCalendar { date ->
                paymentDeadline = date
                updateOrderToDb()
                //viewModel.updateOrderToDb(paymentDeadline = date)
                binding.payDayValueTv.text = date.asDayAndMonthFully()
            }
        }

        binding.paidStatusTv.setOnClickListener {
            if (isPaid) {
                //viewModel.updateOrderToDb(isPaid = false)
                setUnpaidUi(binding.paidStatusTv)
            } else {
                //viewModel.updateOrderToDb(isPaid = true)
                setPaidUi(binding.paidStatusTv)
            }
            isPaid = !isPaid
            updateOrderToDb()
        }

        binding.contractorPaidStatusTv.setOnClickListener {
            if (isPaidToContractor) {
                //viewModel.updateOrderToDb(isPaidToContractor = false)
                setUnpaidUi(binding.contractorPaidStatusTv)
            } else {
                //viewModel.updateOrderToDb(isPaidToContractor = true)
                setPaidUi(binding.contractorPaidStatusTv)
            }
            isPaidToContractor = !isPaidToContractor
            updateOrderToDb()
        }

        binding.callCompanyPhoneBtn.setOnClickListener {
            if (companyPhoneNumber!=null){
                makePhoneCall(companyPhoneNumber!!)
            } else Toast.makeText(requireContext(), "Номер не был найден", Toast.LENGTH_SHORT).show()
        }

        binding.sendWhatsAppBtn.setOnClickListener {
            if (companyPhoneNumber!= null){
                sendMessageToWhatsApp(requireContext(), companyPhoneNumber.toString(), "")
            }else Toast.makeText(requireContext(), "Номер не был найден", Toast.LENGTH_SHORT).show()
        }

        binding.callToManagerBtn.setOnClickListener {
            if (managerPhoneNumber!=null){
                makePhoneCall(managerPhoneNumber!!)
            } else Toast.makeText(requireContext(), "Номер не был найден", Toast.LENGTH_SHORT).show()
        }

        binding.sendWhatsToManagerAppBtn.setOnClickListener {
            if (managerPhoneNumber!= null){
                sendMessageToWhatsApp(requireContext(), managerPhoneNumber.toString(), "")
            }else Toast.makeText(requireContext(), "Номер не был найден", Toast.LENGTH_SHORT).show()
        }

        binding.copyBtn.setOnClickListener {
            val textToCopy = binding.docsNumber.text.toString()
            if (sentDocsNumber!= null) {
                copyToClipboard(textToCopy)
            }else {
                Toast.makeText(requireContext(), "Поле пустое", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initUi(order: Order) {
        Log.i("OrderFrag", "init ui in order: $order")
        isPaid = order.isPaidByCustomer
        routeId = order.routeId
        companyPhoneNumber = order.customer?.companyPhone
        managerPhoneNumber = order.manager?.phoneNumber
        sentDocsNumber = order.sentDocsNumber

        binding.copyBtn.isGone = sentDocsNumber == null

        //тайтл
        val title = if (order.id == 0L) {
            "Новая заявка"
        } else {
            "Заявка № ${order.id}"
        }

        //(activity as AppCompatActivity).supportActionBar?.title = title
        binding.actionBar.title.text = title

        //надпись с типом погрузки
        val typeOfUploadList = mutableListOf<String>()
        if (order.cargo?.isBackLoad == true) {
            typeOfUploadList.add("Задняя")
        }
        if (order.cargo?.isSideLoad == true) {
            typeOfUploadList.add("Боковая")
        }
        if (order.cargo?.isTopLoad == true) {
            typeOfUploadList.add("Верх")
        }
        val typeOfUploadString = typeOfUploadList.joinToString(separator = "/")


        val cargoText =  "${order.cargo?.cargoName} "
        val extraCargoInfoText = "$typeOfUploadString, ${order.cargo?.cargoWeight} т / ${order.cargo?.cargoVolume} м3"
        val priceText = "${order.price?.toPrettyPrice()} руб., ${order.payType.toRussian()}, ${order.daysToPay} б.дней"


        binding.apply {
            phoneCompanyLayout.isGone = order.customer?.companyPhone == null
            phoneManagerLayout.isGone = order.manager?.phoneNumber == null
            managerLabelTv.isGone = order.manager == null
            managerTv.isGone = order.manager == null

            callCompanyPhoneBtn.text = order.customer?.companyPhone?.toPrettyNumber()
            callToManagerBtn.text = order.manager?.phoneNumber?.toPrettyNumber()
            customerTv.text = order.customer?.nameToShow
            managerTv.text = order.manager?.nameToShow
            cargoTv.text = cargoText

            cargoExtraTv.text = extraCargoInfoText
            priceTv.text =  priceText


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
                setPaidUi(binding.paidStatusTv)
                isPaid = true
            } else {
                setUnpaidUi(binding.paidStatusTv)
                isPaid = false
            }

            if (order.contractor?.company?.id != -1L){
                contractorsPriceLayout.isGone = false
                val contractorsPriceText = "${order.contractorPrice?.toPrettyPrice()}, ${getString(R.string.rub)}, ${order.payTypeToContractor?.toRussian()}, ${order.daysToPayToContractor} б. дней "

                binding.contractorsPrice.text = contractorsPriceText


                if (order.isPaidToContractor){
                    setPaidUi(binding.contractorPaidStatusTv)
                    isPaidToContractor = true
                } else {
                    setUnpaidUi(binding.contractorPaidStatusTv)
                    isPaidToContractor = false
                }
            }
        }


        val pointsList = order.points
        pointsList.forEach {
            val pointText = "${it.location}, ${it.arrivalDate.asDayAndMonthFully()}"
            binding.pointsListLayout.addView(
                TextView(requireContext()).apply {
                    text = pointText
                    setPadding(0, 8,0,0)

                }
            )
        }

        //adapter
        //pointsAdapter = OneLinePointAdapter()
        //binding.pointsRecView.adapter = pointsAdapter
        //pointsAdapter.submitList(order.points)
    }

    private fun copyToClipboard(text: String) {
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("simple text", text)
        clipboard.setPrimaryClip(clip)
        //Toast.makeText(requireContext(), "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show()
    }

    private fun setPaidUi(view: Chip) {
        //binding.paidBtn.text = getString(R.string.mark_as_unpaid)
        view.text = getString(R.string.paid_order)
        view.setChipBackgroundColorResource(R.color.green)
    }

    private fun setUnpaidUi(view: Chip) {
        //binding.paidBtn.text = getString(R.string.mark_as_paid)
        view.text = getString(R.string.unpaid_order)
        view.setChipBackgroundColorResource(R.color.red)
    }

    private fun updateOrderToDb(){
        viewModel.updateOrderToDb(paymentDeadline, sentDocsNumber, docsReceived, isPaid, isPaidToContractor)
    }

    private fun share(order: Order){

        val idInfo = "Заявка № ${order.id} Рейс № ${order.routeId}"
        val pointsInfo = StringBuilder()
        order.points.forEach {
            pointsInfo.append("${it.arrivalDate.asDayAndMonthShortly()} ${it.location} ")

        }
        val isPaidInfo = if (order.isPaidByCustomer) "Оплачен" else "Неоплачен"
        val priceInfo = order.price?.let { "Ставка: $it р. ${order.payType} $isPaidInfo" }
        val isPaidToContractorInfo = if (order.isPaidToContractor) "Оплачен" else "Неоплачен"
        val contractorPriceInfo = order.contractorPrice?.let { "Ставка перевозчика: $it р. ${order.payTypeToContractor} $isPaidToContractorInfo" }
        val commissionInfo = order.commission?.let { "Комиссия: $it р." }
        val customerInfo = order.customer?.let { "Заказчик: ${it.shortName} ${it.companyPhone}"}
        val managerInfo = order.manager?.let { "Логист: ${it.nameToShow} ${it.phoneNumber}" }
        val contractorInfo = order.contractor?.let { "Исполнитель: ${it.company?.shortName} ${it.company?.companyPhone}, Перевозчик: ${it.driver?.nameToShow} ${it.truck?.nameToShow} ${it.trailer?.nameToShow}" }
        val cargoInfo = order.cargo?.let { "Груз ${it.cargoName} ${it.cargoWeight}т/${it.cargoVolume}м3 ${order.extraConditions}" }
        val daysToPayInfo = order.daysToPay?.let { "Срок оплаты $it дней" }


        val infoToShare = listOfNotNull(idInfo, customerInfo, managerInfo, contractorInfo,  cargoInfo, pointsInfo, priceInfo, contractorPriceInfo,
             commissionInfo,  daysToPayInfo).joinToString(", ")

        requireContext().shareMessage(infoToShare)

    }

    private fun showMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.menu_remove)
        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item->
            when (item.itemId) {
                R.id.remove_menu_item -> {
                    showDeleteConfirmationDialog("заявку ${currentOrder?.id}") {
                        currentOrder?.id?.let { viewModel.removeById(it) }
                    }
                    //findNavController().navigateUp()

                }
                R.id.share_menu_item -> {
                    currentOrder?.let { share(it) }
                }

                else -> Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show()
            }
            true
        })
        menu.show()
    }


}