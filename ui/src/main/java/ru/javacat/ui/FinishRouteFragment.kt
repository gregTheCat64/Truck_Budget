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
import ru.javacat.ui.databinding.FragmentFinishRouteBinding
import ru.javacat.ui.view_models.FinishRouteViewModel
import java.time.Period

@AndroidEntryPoint
class FinishRouteFragment : BaseFragment<FragmentFinishRouteBinding>() {

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
                binding.prepayEditText.setText(it.prepayment.toString())
                val firstDate = it.orderList.firstOrNull()?.points?.first()?.arrivalDate
                val lastDate = it.orderList.lastOrNull()?.points?.last()?.arrivalDate

                val period = Period.between(firstDate, lastDate)
                val routeDuration = period.days
                binding.routeDaysCount.setText(routeDuration.toString())

                it.driverSalary.let { driverSalary ->
                    if (driverSalary != null) binding.salaryEditText.setText(driverSalary.toString())
                }
                it.income.let { income->
                    if (income != null) binding.incomeTv.text = income.toString()
                }
                it.netIncome.let { netIncome->
                    if (netIncome != null) binding.netIncomeTv.setText(netIncome.toString())
                }


                //binding.fuelCost.setText(it.fuelPrice.toString())
                //currentRoute = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it is LoadState.Success.GoBack) findNavController().navigate(R.id.routeListFragment)
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