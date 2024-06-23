package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.CountRoute
import ru.javacat.domain.models.Route
import ru.javacat.ui.databinding.FragmentFinishRouteBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.FinishRouteViewModel
import java.time.Period
import kotlin.math.roundToInt
import kotlin.time.times

@AndroidEntryPoint
class FinishRouteFragment : BaseFragment<FragmentFinishRouteBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: FinishRouteViewModel by viewModels()

    private var prepay: Int = 0
    private var otherExpenses: Int? = null

    private var routeDuration: Int = 0
    private var payPerDiem: Int? = null

    private var fuelUsedUp: Int? = null
    private var fuelPrice: Float? = null

    private var revenue: Int? = null

    private var salary: Int? = null
    private var profit: Int? = null
    private var moneyToPay: Int? = null

    private var currentRoute: Route? = null
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentFinishRouteBinding
        get() = { inflater, container ->
            FragmentFinishRouteBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val routeId = args?.getLong(FragConstants.ROUTE_ID)
        Log.i("FinishRouteFrag", "routeId: $routeId")

        viewLifecycleOwner.lifecycleScope.launch {
            if (routeId != null) {
                viewModel.getEditedRoute(routeId)
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                Log.i("FinishRouteFrag", "route: $it")
                currentRoute = it

                prepay = it.countRoute?.prepayment ?: 0
                routeDuration = countRouteDuration(it)

                if (!it.isFinished) {
                    getDataFromLastRoute()
                } else getDataFromCurrentRoute(it)
            }
        }

        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it is LoadState.Success.GoBack) findNavController().navigate(R.id.viewPagerFragment)
            }
        }

        binding.calculateSalaryBtn.setOnClickListener {
            if (getFieldsData()) {
                currentRoute?.let { route -> calculateRouteRevenue(route) }
                val countedSalary = calculateSalary()
                binding.salaryEditText.setText(countedSalary.toString())
                binding.revenueTv.setText(revenue.toString())
            } else Toast.makeText(
                requireContext(),
                getString(R.string.fill_requested_fields),
                Toast.LENGTH_SHORT
            ).show()

        }

        binding.calculateRouteBtn.setOnClickListener {
            if (getFieldsWithSalary()) {
                profit = calculateProfit()
                moneyToPay = calculateMoneyToPay()
                binding.profitTv.setText(profit.toString())
                binding.moneyToPayValue.setText(moneyToPay.toString())
            } else Toast.makeText(
                requireContext(),
                getString(R.string.fill_requested_fields),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.saveBtn.setOnClickListener {
            if (getFieldsData() && revenue != null && salary != null && profit != null && fuelPrice != null) {
                viewModel.saveRoute(
                    prepay,
                    otherExpenses!!,
                    routeDuration,
                    fuelUsedUp!!,
                    fuelPrice!!,
                    salary!!,
                    payPerDiem!!,
                    moneyToPay!!,
                    revenue!!,
                    profit!!
                )
            } else Toast.makeText(
                requireContext(),
                "Заполните все поля и нажмите кнопку принять",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.roundBtn.setOnClickListener {
            var inputSalary = if (binding.salaryEditText.text?.isNotEmpty() == true){
                binding.salaryEditText.text?.toString()?.toInt()
            } else null

            if (inputSalary!= null){
                inputSalary = roundToNearestMultiple(inputSalary, 500)
                binding.salaryEditText.setText(inputSalary.toString())
            } else Toast.makeText(requireContext(), "Salary field shouldn't be empty", Toast.LENGTH_SHORT).show()
        }

        binding.add500btn.setOnClickListener {
            var inputSalary = if (binding.salaryEditText.text?.isNotEmpty() == true){
                binding.salaryEditText.text?.toString()?.toInt()
            } else null
            if (inputSalary != null){
                inputSalary = add500(inputSalary!!)
                binding.salaryEditText.setText(inputSalary.toString())
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initUI() {
        prepay.let {
            binding.prepayEditText.setText(it.toString())
        }
        otherExpenses?.let {
            binding.otherExpenses.setText(it.toString())
        }
        fuelUsedUp?.let {
            binding.fuelUsedUp.setText(it.toString())
        }
        fuelPrice?.let {
            binding.fuelPrice.setText(it.toString())
        }
        payPerDiem?.let {
            binding.payPerDiem.setText(it.toString())
        }
        routeDuration.let {
            binding.routeDaysCount.setText(it.toString())
        }
        salary?.let {
            binding.salaryEditText.setText(it.toString())
        }
    }


    private fun getFieldsData(): Boolean {
        if (!binding.prepayEditText.text.isNullOrEmpty()) {
            prepay = binding.prepayEditText.text.toString().toInt()
        } else return false

        if (!binding.otherExpenses.text.isNullOrEmpty()) {
            otherExpenses = binding.otherExpenses.text.toString().toInt()
        } else return false

        if (!binding.routeDaysCount.text.isNullOrEmpty()) {
            routeDuration = binding.routeDaysCount.text.toString().toInt()
        } else return false

        if (!binding.payPerDiem.text.isNullOrEmpty()) {
            payPerDiem = binding.payPerDiem.text.toString().toInt()
        } else return false

        if (!binding.fuelUsedUp.text.isNullOrEmpty()) {
            fuelUsedUp = binding.fuelUsedUp.text.toString().toInt()
        } else return false

        if (!binding.fuelPrice.text.isNullOrEmpty()) {
            fuelPrice = binding.fuelPrice.text.toString().toFloat()
        } else return false



        return true
    }

    private fun getFieldsWithSalary(): Boolean{
        if (!getFieldsData()) return false
        if (!binding.salaryEditText.text.isNullOrEmpty()) {
            salary = binding.salaryEditText.text.toString().toInt()
        } else return false

        return true
    }

    private fun getDataFromLastRoute() {
        viewModel.getLastRoute()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.lastRoute.collectLatest {
                val lastRoute = viewModel.lastRoute.value
                fuelPrice = lastRoute.countRoute?.fuelPrice
                payPerDiem = lastRoute.countRoute?.payPerDiem
                Log.i("FinishRouteFrag", "fuelPrice: $fuelPrice")
                Log.i("FinishRouteFrag", "lastRoute: $it")
                initUI()
            }
        }
    }

    private fun getDataFromCurrentRoute(route: Route) {
        otherExpenses = route.countRoute?.otherExpenses
        payPerDiem = route.countRoute?.payPerDiem
        fuelUsedUp = route.countRoute?.fuelUsedUp
        fuelPrice = route.countRoute?.fuelPrice
        revenue = route.revenue
        salary = route.countRoute?.driverSalary
        profit = route.profit
        moneyToPay = route.countRoute?.moneyToPay

        initUI()
    }

    private fun calculateRouteRevenue(route: Route) {
        revenue = 0
        val orders = route.orderList
        for (i in orders) {
            revenue = revenue?.plus(i.price)
        }
        Log.i("FinishRouteFrag", "revenue: $revenue")
    }

    private fun calculateSalary(): Int? {
        return if (revenue != null && fuelPrice != null) {
            ((revenue!! - ((fuelUsedUp!! * fuelPrice!!) + (routeDuration * payPerDiem!!) + otherExpenses!!)) / 5).roundToInt()
        } else null
    }

    private fun calculateProfit(): Int? {
        return if (revenue != null && salary != null && fuelPrice != null) {
            (revenue!! - (salary!! + (fuelPrice!! * fuelUsedUp!!) + (payPerDiem!! * routeDuration) + otherExpenses!!)).toInt()
        } else null
    }

    private fun countRouteDuration(route: Route): Int {
        val firstDate = route.orderList.firstOrNull()?.points?.first()?.arrivalDate
        val lastDate = route.orderList.lastOrNull()?.points?.last()?.arrivalDate

        val period = Period.between(firstDate, lastDate)
        return period.days
    }

    private fun calculateMoneyToPay(): Int? {
        if (salary != null && fuelPrice != null) {
            return ((prepay - (fuelPrice!! * fuelUsedUp!!) - (payPerDiem!! * routeDuration!!) - otherExpenses!! - salary!!).toInt())
        } else return null
    }

    private fun roundToNearestMultiple(number: Int, multiple: Int): Int {
        return (Math.round(number.toDouble() / multiple) * multiple).toInt()
    }

    private fun add500(number: Int): Int{
        return number + 500
    }


}