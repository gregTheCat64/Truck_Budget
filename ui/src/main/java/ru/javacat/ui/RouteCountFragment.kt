package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.databinding.FragmentRouteCountBinding
import ru.javacat.ui.view_models.RouteCountViewModel
import ru.javacat.ui.view_models.RouteViewModel

@AndroidEntryPoint
class RouteCountFragment : BaseFragment<FragmentRouteCountBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: RouteCountViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteCountBinding
        get() = { inflater, container ->
            FragmentRouteCountBinding.inflate(inflater, container, false)
        }

    private var currentRoute: Route? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest {
                    if (it != null) {
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
        currentRoute = route
        binding.prepayTv.text = route.prepayment?.toString()

        val currentIncome = (route.orderList.map { it.price }).sum()
        val subsistenceExp =
            route.routeDuration?.let { it1 -> route.payPerDiem?.times(it1).toString() + " руб" }
        val fuelSpending =
            route.fuelUsedUp?.let { it1 -> route.fuelPrice?.times(it1).toString() + " руб" }
        val routeSpending = route.routeSpending?.toString() + " руб"
        val salary = route.driverSalary?.toString() + " руб"
        val income = route.income?.toString() + " руб"
        val netIncome = route.netIncome?.toString() + " руб"

        if (route.isFinished && route.income != currentIncome){
            Toast.makeText(requireContext(), "Изменилась выручка, пересчитайте рейс", Toast.LENGTH_SHORT).show()
        }

        binding.editCountBtn.isGone = currentRoute!!.orderList.isEmpty() || !currentRoute!!.isFinished

        binding.finalCountFrame.isGone = !currentRoute!!.isFinished

        binding.subsistenceExpensesTv.setText("${route.payPerDiem} руб. * ${route.routeDuration} = $subsistenceExp")
        binding.fuelPriceTv.setText("${route.fuelPrice} руб. * ${route.fuelUsedUp} л. = $fuelSpending")
        binding.otherSpendingTv.text = routeSpending
        binding.salaryTv.text = salary
        binding.incomeTv.text = income
        binding.netIncomeTv.text = netIncome

        //binding.finalCountFrame.isGone = true
    }

    private fun calculate(){
        if (currentRoute?.orderList?.isNotEmpty() == true){
            //viewModel.setRouteFinished()
            findNavController().navigate(R.id.finishRouteFragment)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

}