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
import ru.javacat.ui.OneInputValueDialogFragment
import ru.javacat.ui.utils.makePhoneCall
import ru.javacat.ui.utils.sendMessageToWhatsApp

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
                println("НОМЕР ПРИШЕЛ: $docsNumber")
                sentDocsNumber = it
                updateOrderToDb()
                binding.docsNumber.text = it
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

        binding.backBtn.setOnClickListener {
            if (isNewOrder == true) {
                findNavController().popBackStack(R.id.routeViewPagerFragment, false)
            } else {
            findNavController().navigateUp()
        }
        }

        binding.editBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(FragConstants.ORDER_ID, orderIdArg ?: 0)
            bundle.putLong(FragConstants.ROUTE_ID, routeId ?:0)
            bundle.putBoolean(FragConstants.EDITING_ORDER, true)

            findNavController().navigate(R.id.action_orderFragment_to_editOrderFragment, bundle)
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


    }

    private fun initUi(order: Order) {
        Log.i("OrderFrag", "order: $order")
        isPaid = order.isPaidByCustomer
        routeId = order.routeId
        companyPhoneNumber = order.customer?.companyPhone
        managerPhoneNumber = order.manager?.phoneNumber
        sentDocsNumber = order.sentDocsNumber

        //тайтл
        val title = if (order.id == 0L) {
            "Новая заявка"
        } else {
            "Заявка № ${order.id}"
        }

        //(activity as AppCompatActivity).supportActionBar?.title = title
        binding.title.text = title

        //надпись с типом погрузки
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

        val cargoText =  "${order.cargo?.cargoName}, ${order.cargo?.cargoWeight} т / ${order.cargo?.cargoVolume} м3 "
        val priceText = "${order.price} руб., ${order.payType.toRussian()}, ${order.daysToPay} б.дней"


        binding.apply {
            phoneCompanyLayout.isGone = order.customer?.companyPhone == null
            phoneManagerLayout.isGone = order.manager?.phoneNumber == null
            managerLabelTv.isGone = order.manager == null
            managerTv.isGone = order.manager == null

            callCompanyPhoneBtn.text = order.customer?.companyPhone
            callToManagerBtn.text = order.manager?.phoneNumber
            customerTv.text = order.customer?.nameToShow
            managerTv.text = order.manager?.nameToShow
            cargoTv.text = cargoText

            cargoExtraTv.text = typeOfUploadString
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
                val contractorsPriceText = "${order.contractorPrice.toString()}, ${getString(R.string.rub)}"
                val contractorPriceExtraText =  "${order.payTypeToContractor?.toRussian()}, ${order.daysToPayToContractor} б. дней"

                binding.contractorsPrice.text = contractorsPriceText
                binding.contractorPriceExtraTv.text = contractorPriceExtraText

                if (order.isPaidToContractor){
                    setPaidUi(binding.contractorPaidStatusTv)
                    isPaidToContractor = true
                } else {
                    setUnpaidUi(binding.contractorPaidStatusTv)
                    isPaidToContractor = false
                }
            }
        }

        //adapter
        pointsAdapter = OneLinePointAdapter()
        binding.pointsRecView.adapter = pointsAdapter
        pointsAdapter.submitList(order.points)
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


}