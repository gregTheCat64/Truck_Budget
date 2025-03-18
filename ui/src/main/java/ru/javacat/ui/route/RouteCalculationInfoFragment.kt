package ru.javacat.ui.route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import ru.javacat.common.utils.toPrettyPrice
import ru.javacat.domain.models.Route
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentRouteCalculationInfoBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showDeleteConfirmationDialog
import kotlin.math.roundToInt

@AndroidEntryPoint
class RouteCalculationInfoFragment : BaseFragment<FragmentRouteCalculationInfoBinding>() {

    private val viewModel: RouteCalculationInfoViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteCalculationInfoBinding
        get() = { inflater, container ->
            FragmentRouteCalculationInfoBinding.inflate(inflater, container, false)
        }

    private var currentRoute: Route? = null
    var routeId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = arguments
        routeId = args?.getLong(FragConstants.ROUTE_ID)

        Log.i("RouteCalcInfo", "routeId: $routeId")

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        var isPaid = false

        //чекаем стейты
        stateListener()

        //получаем текущий рейс
        routeId?.let { viewModel.updateCurrentRoute(it) }

        //инициализация UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest {
                    if (it != null) {
                        currentRoute = it
                        isPaid = it.isPaidToContractor
                        initUi(it)
                    }
                }
            }
        }

        //редактирование рейса
        binding.actionBar.editBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, routeId?:0)
            findNavController().navigate(R.id.newRouteFragment, bundle)
        }

        binding.actionBar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.actionBar.moreBtn.setOnClickListener {
            showMenu(it)
        }

        //рассчет рейса и финиш
        binding.calculateBtn.setOnClickListener {
            toCalculation()
        }

        binding.contractorName.setOnClickListener {
            toCompanyFragment()
        }

        //статус оплаты
        binding.paymentChip.setOnClickListener {
            if (isPaid) {
                viewModel.setPaid(isPaid = false)
                setUnpaid()
            } else {
                viewModel.setPaid(isPaid = true)
                setPaid()
            }

        }
    }

    private fun stateListener(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    if (it == LoadState.Success.GoBack) findNavController().navigateUp()
                }
            }
        }
    }

    private fun initUi(route: Route) {
        val title = "Рейс № ${route.id}"
        binding.actionBar.title.text = title
        val currentIncome = (route.orderList.map { it.price!! }).sum()

        if (route.isPaidToContractor) {
            setPaid()
        } else setUnpaid()

        if (route.isFinished && route.revenue != currentIncome) {
            Toast.makeText(
                requireContext(),
                "Изменилась выручка, пересчитайте рейс",
                Toast.LENGTH_SHORT
            ).show()
        }


        binding.contractorName.text = route.contractor!!.company!!.nameToShow
        binding.truckDriverName.text = route.contractor!!.driver?.nameToShow
        binding.vehicleInfo.text = route.contractor!!.truck?.nameToShow

        //TODO привезти этот блок в порядок
            //Моя компания
        if (route.contractor?.company?.id == FragConstants.MY_COMPANY_ID) {
            if (route.contractor?.driver?.id != -1L) {
                //Мой водитель

                binding.myTransportLayout.isGone = false
                binding.contractorLayout.isGone = true

                val subsistenceExp = route.salaryParameters?.costPerDiem?.let {
                    route.routeDetails?.routeDuration?.times(it)
                }

                val fuelSpending =
                    route.routeDetails?.fuelPrice?.let { route.routeDetails?.fuelUsedUp?.times(it) }
                val fuelSpendingString = if (fuelSpending != null) {
                    "${route.routeDetails?.fuelPrice} ${getString(R.string.rub)} * ${route.routeDetails?.fuelUsedUp} л. = $fuelSpending"
                } else ""

                binding.prepayTv.text = route.prepayment.toString()
                binding.fuelCostTv.text = fuelSpendingString
                subsistenceExp?.let {
                    binding.subsistenceExpensesTv.text = "${it} ${getString(R.string.rub)}"
                }
                route.routeDetails?.roadFee?.let {
                    binding.roadFee.text = "$it ${getString(R.string.rub)}"
                }
                route.routeDetails?.extraExpenses?.let {
                    binding.otherSpendingTv.text = "$it ${getString(R.string.rub)}"
                }
                route.driverSalary?.let {
                    binding.salaryTv.text = "$it ${getString(R.string.rub)}"
                }
            } else {
                //сам зарулем
                binding.contractorName.text = route.contractor!!.driver?.nameToShow
                binding.prepayLayout.isGone = true
                binding.salaryLayout.isGone = true
                binding.subsistenceLayout.isGone = true
                binding.moneyToPayLayout.isGone = true
                binding.paymentChip.isGone = true

                val fuelSpending =
                    route.routeDetails?.fuelPrice?.let { route.routeDetails?.fuelUsedUp?.times(it) }
                val fuelSpendingString = if (fuelSpending != null) {
                    "${route.routeDetails?.fuelPrice} ${getString(R.string.rub)} * ${route.routeDetails?.fuelUsedUp} л. = $fuelSpending"
                } else ""

                binding.prepayTv.text = route.prepayment.toString()
                binding.fuelCostTv.text = fuelSpendingString

                route.routeDetails?.roadFee?.let {
                    binding.roadFee.text = "$it ${getString(R.string.rub)}"
                }
                route.routeDetails?.extraExpenses?.let {
                    binding.otherSpendingTv.text = "$it ${getString(R.string.rub)}"
                }
            }
        } else {
            //привлеченный транспорт
            binding.myTransportLayout.isGone = true
        }

        //если рейс не завершен убираем лишнюю информацию
        if (route.isFinished){
            binding.calculateBtn.text = "Пересчитать"
            binding.paymentChip.isGone = false
        } else {
            binding.myTransportLayout.isGone = true
            binding.resultInfoLayout.isGone = true
        }

        //кнопка редактирования видима если рейс не пустой или рейс завершен
        binding.calculateBtn.isGone = route.orderList.isEmpty()

        route.profit?.let {
            binding.profitTv.text = "$it ${getString(R.string.rub)}"
        }

        route.moneyToPay?.let {
            if (route.moneyToPay!! < 0F ){
                binding.moneyToPayLabel.text = "Остаток с рейса"
                binding.moneyToPayTv.text = "${it*-1} ${getString(R.string.rub)}"
            } else {
                binding.moneyToPayTv.text = "$it ${getString(R.string.rub)}"
            }

        }

        route.revenue?.let {
            binding.revenueTv.text = "$it ${getString(R.string.rub)}"
        }

    }

    private fun toCalculation() {
        if (currentRoute?.contractor?.company?.id == -1L){
            if (currentRoute?.contractor?.driver?.id == -1L){
                toFinishRouteWithOutDriverFragment()
            } else {
                toFinishRouteFragment()
            }
        } else toFinishPartnerRouteFragment()
    }

    private fun setPaid() {
        binding.paymentChip.apply {
            isChecked = true
            text = getString(R.string.paid_order)
        }
    }

    private fun setUnpaid() {
        binding.paymentChip.apply {
            isChecked = false
            text = getString(R.string.unpaid_order)
        }
    }

    private fun toFinishRouteFragment(){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            findNavController().navigate(R.id.finishRouteFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toFinishRouteWithOutDriverFragment(){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            findNavController().navigate(R.id.finishRouteWithoutDriverFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toFinishPartnerRouteFragment(){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            val bundle = Bundle()
            bundle.putLong(FragConstants.ROUTE_ID, currentRoute?.id?:0)
            findNavController().navigate(R.id.finishPartnerRouteFragment, bundle)
        }else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun toCompanyFragment(){
        val bundle = Bundle()
        bundle.putLong(FragConstants.CUSTOMER_ID, currentRoute?.contractor?.company?.id?:-1)
        findNavController().navigate(R.id.action_routeCountFragment_to_companyFragment, bundle)
    }

    private fun showMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.menu_remove)
        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item->
            when (item.itemId) {
                R.id.remove_menu_item -> {
                    showDeleteConfirmationDialog("рейс $routeId"){
                        routeId?.let { viewModel.removeRoute(it) }
                    }
                }
                R.id.share_menu_item -> {
                    Toast.makeText(requireContext(), "Пока не реализовано", Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(context, "Пока не реализовано", Toast.LENGTH_SHORT).show()
            }
            true
        })
        menu.show()
    }

}