package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.databinding.FragmentFinishRouteBinding
import ru.javacat.ui.view_models.FinishRouteViewModel
import java.time.Period

@AndroidEntryPoint
class FinishRouteFragment : BaseFragment<FragmentFinishRouteBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: FinishRouteViewModel by viewModels()

    private var prepay: Int = 0
    private var routeSpending: Int = 0

    private var routeDuration: Int = 0
    private var payPerDiem: Int = 0

    private var fuelUsedUp: Int = 0
    private var fuelPrice: Float = 0f

    private var salary: Int? = 0
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentFinishRouteBinding
        get() = { inflater, container ->
            FragmentFinishRouteBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                if (it != null) {
                    initUI(it)
                }
            }
        }

        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it is LoadState.Success.GoBack) findNavController().navigate(R.id.viewPagerFragment)
            }
        }

        binding.calculateBtn.setOnClickListener {
            getFieldsData()
            viewModel.calculateSalary()
        }

        binding.calculateRouteBtn.setOnClickListener {
            getFieldsData()
            viewModel.calculateNetIncome()
        }

        binding.saveBtn.setOnClickListener {
            viewModel.saveRoute()
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initUI(route: Route){

        val firstDate = route.orderList.firstOrNull()?.points?.first()?.arrivalDate
        val lastDate = route.orderList.lastOrNull()?.points?.last()?.arrivalDate

        val period = Period.between(firstDate, lastDate)
        val routeDuration = period.days
        binding.routeDaysCount.setText(routeDuration.toString())

        route.prepayment?.let {
            binding.prepayEditText.setText(it.toString())
        }

        route.driverSalary?.let { driverSalary ->
            binding.salaryEditText.setText(driverSalary.toString())
        }
        route.income?.let { income->
            binding.incomeTv.text = income.toString()
        }
        route.netIncome?.let { netIncome->
            binding.netIncomeTv.setText(netIncome.toString())
        }
        route.routeSpending?.let {
            binding.routeSpending.setText(it.toString())
        }

        route.fuelPrice?.let {
            binding.fuelPrice.setText(it.toString())
        }

        route.fuelUsedUp?.let {
            binding.fuelUsedUp.setText(it.toString())
        }
        route.payPerDiem?.let {
            binding.payPerDiem.setText(it.toString())
        }

        route.moneyToPay?.let {
            binding.moneyToPayValue.setText(it.toString())
        }
    }

    private fun getFieldsData() {
        prepay = binding.prepayEditText.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        } ?: 0

        routeSpending = binding.routeSpending.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        } ?: 0

        routeDuration = binding.routeDaysCount.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        } ?: 0

        payPerDiem = binding.payPerDiem.text?.let {
            if (it.isBlank()) 0 else it.toString().toInt()
        } ?: 0

        fuelUsedUp = binding.fuelUsedUp.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        } ?: 0

        fuelPrice = binding.fuelPrice.text?.let {
            if (it.isBlank()) null else it.toString().toFloat()
        } ?: 0f

        salary = binding.salaryEditText.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        }

        viewModel.setFieldsData(prepay, routeSpending, routeDuration, fuelUsedUp, fuelPrice, payPerDiem, salary)
    }


}