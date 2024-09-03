package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.databinding.FragmentRouteCalculationInfoBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.RouteCountViewModel

@AndroidEntryPoint
class RouteCalculationInfoFragment : BaseFragment<FragmentRouteCalculationInfoBinding>() {

    private val viewModel: RouteCountViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteCalculationInfoBinding
        get() = { inflater, container ->
            FragmentRouteCalculationInfoBinding.inflate(inflater, container, false)
        }

    private var currentRoute: Route? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = arguments
        val routeId = args?.getLong(FragConstants.ROUTE_ID)

        Log.i("RouteCalcInfo", "routeId: $routeId")

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest {
                    if (it!=null){
                        currentRoute = it
                        initUi(it)
                    }
                }
            }
        }

        binding.editCountBtn.setOnClickListener {
            calculate()
        }
    }

    private fun initUi(route: Route) {
        //TODO перенести этот блок на основнюу страницу:
        val currentIncome = (route.orderList.map { it.price!! }).sum()

        if (route.isFinished && route.revenue != currentIncome){
                Toast.makeText(requireContext(), "Изменилась выручка, пересчитайте рейс", Toast.LENGTH_SHORT).show()
            }

        if (route.contractor?.company?.id == FragConstants.MY_COMPANY_ID){
            binding.myTransportLayout.isVisible = true

            val subsistenceExp = route.salaryParameters?.costPerDiem?.let { route.routeDetails?.routeDuration?.times(it) }

            val fuelSpending = route.routeDetails?.fuelPrice?.let { route.routeDetails?.fuelUsedUp?.times(it) }
            val fuelSpendingString = if (fuelSpending!=null){
                "${route.routeDetails?.fuelPrice} ${getString(R.string.rub)} * ${route.routeDetails?.fuelUsedUp} л. = $fuelSpending"
            } else ""

            binding.prepayTv.text = route.prepayment.toString()
            binding.fuelCostTv.text = fuelSpendingString
            subsistenceExp?.let {
                binding.subsistenceExpensesTv.text = "${it} ${getString(R.string.rub)}"
            }
            route.routeDetails?.extraExpenses?.let {
                binding.otherSpendingTv.text = "$it ${getString(R.string.rub)}"
            }
            route.driverSalary?.let {
                binding.salaryTv.text = "$it ${getString(R.string.rub)}"
            }

        } else {
            binding.myTransportLayout.isGone = true
        }


        //кнопка редактирования видима если рейс не пустой или рейс завершен
        binding.editCountBtn.isGone = route.orderList.isEmpty() || !route.isFinished

        route.profit?.let {
            binding.profitTv.text = "$it ${getString(R.string.rub)}"
        }
        route.moneyToPay?.let {
            binding.moneyToPayTv.text = "$it ${getString(R.string.rub)}"
        }

        route.revenue?.let {
            binding.revenueTv.text = "$it ${getString(R.string.rub)}"
        }

    }

    private fun calculate(){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            findNavController().navigate(R.id.finishRouteFragment)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

}