package ru.javacat.ui

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
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.SalaryCountMethod
import ru.javacat.domain.models.TruckDriver
import ru.javacat.ui.databinding.FragmentFinishRouteBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.FinishRouteViewModel
import java.time.LocalDate
import java.time.Period

@AndroidEntryPoint
class FinishRouteFragment : BaseFragment<FragmentFinishRouteBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: FinishRouteViewModel by viewModels()

    private var prepay: Int? = null
    private var extraExpenses: Int? = null
    private var roadFee: Int? = null

    private var routeDuration: Int? = null
    private var extraPoints: Int? = null

    private var fuelUsedUp: Int? = null
    private var fuelPrice: Float? = null
//
    private var revenue: Int? = null
//
    private var salary: Float? = null
    private var totalExpenses: Float? = null
    private var profit: Float? = null
    private var moneyToPay: Float? = null
//
    private var salaryCountMethod: SalaryCountMethod? = SalaryCountMethod.BY_PROFIT
    private var profitPercentage: Int? = null
    private var routeDistance: Int? = null
    private var costPerDiem: Int? = null
    private var costPerKilometer: Float? = null
    private var extraPointsCost: Int? = null

    private var lastDate: LocalDate? = null

    private var currentDriver: TruckDriver? = null
    private var currentRoute: Route? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentFinishRouteBinding
        get() = { inflater, container ->
            FragmentFinishRouteBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_cancel, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    R.id.cancel_button_menu_item -> {
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

        val args = arguments
        val routeId = args?.getLong(FragConstants.ROUTE_ID)
        Log.i("FinishRouteFrag", "routeId: $routeId")

        //Salary count
        binding.profitSalaryChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.percentOfProfit.isGone = false
                binding.distanceLayout.isGone = true
                binding.distanceSalaryChip.isChecked = false

                salaryCountMethod = SalaryCountMethod.BY_PROFIT
            }
        }

        binding.distanceSalaryChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.percentOfProfit.isGone = true
                binding.distanceLayout.isGone = false
                binding.profitSalaryChip.isChecked = false

                salaryCountMethod = SalaryCountMethod.BY_DISTANCE
            }
        }

        binding.extraExpenseAddToPrepayBtn.setOnClickListener{
            if (!binding.otherExpenses.text.isNullOrEmpty()){
                extraExpenses = binding.otherExpenses.text?.toString()?.toInt()?:0
                prepay = prepay?.plus(extraExpenses!!)
                binding.prepayEditText.setText(prepay.toString())
            } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
        }

        binding.roadFeeAddToPrepayBtn.setOnClickListener {
            if (!binding.roadFee.text.isNullOrEmpty()){
                roadFee = binding.roadFee.text?.toString()?.toInt()?:0
                prepay = prepay?.plus(roadFee!!)
                binding.prepayEditText.setText(prepay.toString())
            } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            if (routeId != null) {
                viewModel.getEditedRoute(routeId)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                Log.i("FinishRouteFrag", "route: $it")
                if (it != null){
                    currentRoute = it
                    prepay = it.prepayment ?: 0
                    extraPoints = calculateExtraPoints()
                    routeDuration = countRouteDuration(it)

                    if (!it.isFinished) {
                        getDataFromLastRoute()
                    } else getDataFromCurrentRoute(it)
                }

            }
        }

        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it is LoadState.Success.GoBack) findNavController().navigate(R.id.routeViewPagerFragment)
            }
        }



        //считаем зарплату
        binding.calculateSalaryBtn.setOnClickListener {
            if (getFieldsData()) {
                revenue = currentRoute?.let { route -> calculateRouteRevenue(route) }
                val fuelCost = fuelUsedUp?.let { it1 -> fuelPrice?.times(it1) }
                val payDayResult = routeDuration?.let { it1 -> costPerDiem?.times(it1) }
                val extraPointsResult = extraPointsCost?.let { it1 -> extraPoints?.times(it1) }
                val countedSalary = calculateSalary()

                binding.fuelCost.setText("Итого: $fuelCost руб.")
                binding.payDayResult.setText("Итого: $payDayResult руб.")
                binding.extraPointsResult.setText("Итого: $extraPointsResult руб.")
                binding.salaryEditText.setText(countedSalary.toString())
                binding.revenueTv.setText(revenue.toString())
            } else {
                println("salary = $salaryCountMethod, revenue = $revenue, extrExp = $extraExpenses, fuelPrice = $fuelPrice," +
                        " fuelUsedUp = $fuelUsedUp, routeDur = $routeDuration, costPerDiem = $costPerDiem, salaryCountMethod = $salaryCountMethod" +
                        "profitPerc = $profitPercentage, roadFee = $roadFee, routeDistanse = $routeDistance, costPerKm = $costPerKilometer"
                )
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fill_requested_fields),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        //считаем прибыль
        binding.calculateRouteBtn.setOnClickListener {
            if (getFieldsWithSalary()) {
                binding.saveBtn.isGone = false
                totalExpenses = calculateTotalExpenses()
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
            if (getFieldsData() && revenue != null && salary != null && profit != null && fuelPrice != null && fuelUsedUp != null) {
                viewModel.saveRoute(
                    extraPoints?:0,
                    prepay?:0,
                    extraExpenses?:0,
                    roadFee?:0,
                    routeDuration?:1,
                    routeDistance?:0,
                    fuelUsedUp!!,
                    fuelPrice!!,
                    salary!!,
                    costPerDiem?:0,
                    moneyToPay?:0f,
                    revenue!!,
                    profit!!,
                    salaryCountMethod?:SalaryCountMethod.BY_DISTANCE,
                    profitPercentage?:0,
                    costPerKilometer?:0f,
                    extraPointsCost?:0,
                    lastDate,
                    totalExpenses?:0f
                )
            } else Toast.makeText(
                requireContext(),
                "Заполните все поля и нажмите кнопку принять",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.roundBtn.setOnClickListener {
            var inputSalary = if (binding.salaryEditText.text?.isNotEmpty() == true){
                binding.salaryEditText.text?.toString()?.toFloat()?.toInt()
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
    }

    private fun updateRouteDetailsUi() {
        prepay?.let {
            binding.prepayEditText.setText(it.toString())
        }
        extraExpenses?.let {
            binding.otherExpenses.setText(it.toString())
        }
        roadFee?.let {
            binding.roadFee.setText(it.toString())
        }
        extraPoints?.let {
            binding.extraPointsValue.setText(it.toString())
        }
        fuelUsedUp?.let {
            binding.fuelUsedUp.setText(it.toString())
        }
        fuelPrice?.let {
            binding.fuelPrice.setText(it.toString())
        }
        routeDuration.let {
            binding.routeDaysCount.setText(it.toString())
        }
        salary?.let {
            binding.salaryEditText.setText(it.toString())
        }

        extraPointsCost?.let {
            binding.extraPointsCost.setText(it.toString())
        }
        costPerDiem?.let {
            binding.costPerDiem.setText(it.toString())
        }
        when (salaryCountMethod) {
            SalaryCountMethod.BY_DISTANCE -> {
                binding.distanceSalaryChip.isChecked = true
                binding.profitSalaryChip.isChecked = false
            }
            else -> {
                binding.profitSalaryChip.isChecked = true
                binding.distanceSalaryChip.isChecked = false
            }
        }
        profitPercentage?.let {
            binding.percentOfProfit.setText(it.toString())
        }
        routeDistance?.let {
            binding.routeDistance.setText(it.toString())
        }
        costPerKilometer?.let {
            binding.costPerKilometer.setText(it.toString())
        }
    }


    private fun getFieldsData(): Boolean {
        if (!binding.prepayEditText.text.isNullOrEmpty()) {
            prepay = binding.prepayEditText.text.toString().toInt()
        } else prepay = 0

        if (!binding.otherExpenses.text.isNullOrEmpty()) {
            extraExpenses = binding.otherExpenses.text.toString().toInt()
        } else extraExpenses = 0

        if (!binding.roadFee.text.isNullOrEmpty()) {
            roadFee = binding.roadFee.text.toString().toInt()
        } else roadFee = 0

        if (!binding.extraPointsCost.text.isNullOrEmpty()) {
            extraPointsCost = binding.extraPointsCost.text.toString().toInt()
        } else extraPointsCost = 0

        if (!binding.extraPointsValue.text.isNullOrEmpty()) {
            extraPoints = binding.extraPointsValue.text.toString().toInt()
        } else extraPoints = 0

        if (!binding.routeDaysCount.text.isNullOrEmpty()) {
            routeDuration = binding.routeDaysCount.text.toString().toInt()
        } else routeDuration = 0

        if (!binding.costPerDiem.text.isNullOrEmpty()) {
            costPerDiem = binding.costPerDiem.text.toString().toInt()
        } else costPerDiem = 0

        if (!binding.fuelUsedUp.text.isNullOrEmpty()) {
            fuelUsedUp = binding.fuelUsedUp.text.toString().toInt()
        } else return false

        if (!binding.fuelPrice.text.isNullOrEmpty()) {
            fuelPrice = binding.fuelPrice.text.toString().toFloat()
        } else return false

        when (salaryCountMethod){
            SalaryCountMethod.BY_PROFIT -> {
                if (!binding.percentOfProfit.text.isNullOrEmpty()){
                    profitPercentage = binding.percentOfProfit.text.toString().toInt()
                } else return false
            }
            SalaryCountMethod.BY_DISTANCE -> {
                if (!binding.costPerKilometer.text.isNullOrEmpty()){
                    costPerKilometer = binding.costPerKilometer.text.toString().toFloat()
                    routeDistance = binding.routeDistance.text.toString().toInt()
                } else return false
            }

            null -> return false
        }

        return true
    }

    private fun getFieldsWithSalary(): Boolean{
        if (!getFieldsData()) return false
        if (!binding.salaryEditText.text.isNullOrEmpty()) {
            salary = binding.salaryEditText.text.toString().toFloat()
        } else return false

        return true
    }

    private fun getDataFromLastRoute() {
        println("getDataFromLastRoute")
        viewModel.getLastRoute()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.lastRoute.collectLatest {
                val lastRoute = viewModel.lastRoute.value
                fuelPrice = lastRoute.routeDetails?.fuelPrice

                //берем данные из параметров водителя текущего рейса
                currentDriver = currentRoute?.contractor?.driver

                salaryCountMethod = currentDriver?.salaryParameters?.salaryCountMethod?:SalaryCountMethod.BY_PROFIT
                costPerDiem = currentDriver?.salaryParameters?.costPerDiem
                costPerKilometer  = currentDriver?.salaryParameters?.costPerKilometer
                extraPointsCost = currentDriver?.salaryParameters?.extraPointsCost
                profitPercentage = currentDriver?.salaryParameters?.profitPercentage

                Log.i("FinishRouteFrag", "fuelPrice: $fuelPrice")
                Log.i("FinishRouteFrag", "lastRoute: $it")
                updateRouteDetailsUi()
            }
        }
    }

    private fun getDataFromCurrentRoute(route: Route) {
        println("getDataFromCurrentRoute")
        route.let {
            prepay = it.prepayment
            extraExpenses = it.routeDetails?.extraExpenses
            roadFee = it.routeDetails?.roadFee
            fuelPrice = it.routeDetails?.fuelPrice
            fuelUsedUp = it.routeDetails?.fuelUsedUp
            extraPoints = it.routeDetails?.extraPoints
            routeDuration = it.routeDetails?.routeDuration
            routeDistance = it.routeDetails?.routeDistance
            salary = it.driverSalary
            revenue = it.revenue
            profit = it.profit
            moneyToPay = it.moneyToPay

            salaryCountMethod = it.salaryParameters?.salaryCountMethod
            costPerDiem = it.salaryParameters?.costPerDiem
            extraPointsCost = it.salaryParameters?.extraPointsCost
            costPerKilometer = it.salaryParameters?.costPerKilometer
            profitPercentage = it.salaryParameters?.profitPercentage

            println("salaryParam: ${it.salaryParameters}")
        }
        updateRouteDetailsUi()
    }

    private fun calculateRouteRevenue(route: Route): Int {
        var sumRevenue = 0
        val orders = route.orderList
        for (i in orders) {
            sumRevenue = i.price?.let { sumRevenue.plus(it) }?:0
        }
        Log.i("FinishRouteFrag", "sumRevenue: $sumRevenue")
        return sumRevenue
    }

    private fun calculateSalary(): Float {
        return viewModel.calculateSalary(salaryCountMethod, revenue!!, extraExpenses!!, fuelPrice!!, fuelUsedUp!!,
            routeDuration!!, costPerDiem!!,
            profitPercentage, roadFee!!, routeDistance, costPerKilometer
            )
    }

    private fun calculateTotalExpenses(): Float{
        return viewModel.calculateTotalExpenses(salary!!, fuelUsedUp!!, fuelPrice!!, routeDuration!!, costPerDiem!!,
            extraPoints!!, extraPointsCost!!, extraExpenses!!, 0, roadFee!!)
    }

    private fun calculateProfit(): Float {
        return viewModel.calculateProfit(revenue!!, totalExpenses!!)
    }

    private fun countRouteDuration(route: Route): Int {
        val firstDate = route.orderList.firstOrNull()?.points?.first()?.arrivalDate
        lastDate = route.orderList.lastOrNull()?.points?.last()?.arrivalDate

        val period = Period.between(firstDate, lastDate)
        return period.days
    }

    private fun calculateExtraPoints(): Int {
        var sumPoints = 0
        for (i in currentRoute?.orderList!!){
            sumPoints += i.points.size
        }
        println("sumPoints: $sumPoints")
        return sumPoints - 4
    }

    private fun calculateMoneyToPay(): Float {
       return viewModel.calculateMyDebt(prepay!!, totalExpenses!!)
    }

    private fun roundToNearestMultiple(number: Int, multiple: Int): Int {
        return (Math.round(number.toDouble() / multiple) * multiple).toInt()
    }

    private fun add500(number: Int): Int{
        return number + 500
    }


}