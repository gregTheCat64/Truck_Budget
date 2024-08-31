package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.SalaryCountMethod
import ru.javacat.domain.models.SalaryParameters
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.use_case.CalculateMyDebtUseCase
import ru.javacat.domain.use_case.CalculateProfitUseCase
import ru.javacat.domain.use_case.CalculateTotalExpensesUseCase
import ru.javacat.domain.use_case.CalculateTruckDriverSalaryUseCase
import ru.javacat.ui.LoadState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FinishRouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val calculateTruckDriverSalaryUseCase: CalculateTruckDriverSalaryUseCase,
    private val calculateTotalExpenses: CalculateTotalExpensesUseCase,
    private val calculateProfitUseCase: CalculateProfitUseCase,
    private val calculateMyDebtUseCase: CalculateMyDebtUseCase
) : ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val editedRoute = routeRepository.editedItem
    private val _lastRoute = MutableStateFlow(Route(startDate = LocalDate.now(), salaryParameters = SalaryParameters()))
    val lastRoute: StateFlow<Route>
        get() = _lastRoute.asStateFlow()

    fun calculateSalary(salaryCountMethod: SalaryCountMethod,
                        revenue: Int,
                        extraExpenses: Int,
                        fuelPrice: Float,
                        fuelUsedUp: Int,
                        routeDuration: Int,
                        costPerDiem: Int,
                        profitPercentage: Int,
                        roadFee: Int,
                        routeDistance: Int,
                        costPerKilometer: Float) = calculateTruckDriverSalaryUseCase.invoke(
        salaryCountMethod,revenue, extraExpenses, fuelPrice, fuelUsedUp,
        routeDuration, costPerDiem, profitPercentage, roadFee, routeDistance,
        costPerKilometer)

    fun calculateTotalExpenses(driverSalary: Float,
                               fuelUsedUp: Int,
                               fuelPrice: Float,
                               routeDuration: Int,
                               costPerDiem: Int,
                               extraPoints: Int,
                               extraPointsCost: Int,
                               extraExpenses: Int,
                               contractorsCost: Int,
                               roadFee: Int) = calculateTotalExpenses.invoke(driverSalary, fuelUsedUp, fuelPrice, routeDuration, costPerDiem, extraPoints, extraPointsCost, extraExpenses, contractorsCost, roadFee)

    fun calculateProfit(revenue: Int, totalExpenses: Float) = calculateProfitUseCase.invoke(revenue, totalExpenses)

    fun calculateMyDebt(prepayment: Int, totalExpenses: Float) = calculateMyDebtUseCase.invoke(prepayment, totalExpenses)

    fun getEditedRoute(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val route = routeRepository.getById(routeId)
            if (route != null) {
                routeRepository.updateEditedItem(route)
            }
        }
    }

    fun getLastRoute() {
        viewModelScope.launch(Dispatchers.IO) {
            routeRepository.lastRoute?.let { _lastRoute.emit(it) }
        }
    }


    fun saveRoute(
        extraPoints: Int,
        prepay: Int,
        extraExpenses: Int,
        roadFee: Int?,
        routeDuration: Int,
        fuelUsedUp: Int,
        fuelPrice: Float,
        salary: Float,
        payPerDiem: Int,
        moneyToPay: Float,
        revenue: Int,
        profit: Float,
        salaryCountMethod: SalaryCountMethod,
        profitPercentage: Int?,
        costPerKilometer: Float?,
        extraPointsCost: Int?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val salaryParameters = SalaryParameters(
                    salaryCountMethod,profitPercentage?:0,payPerDiem, costPerKilometer?:0f, extraPointsCost?:0
                )

                editedRoute.value?.copy(
                    prepayment = prepay,
                    fuelUsedUp = fuelUsedUp,
                    fuelPrice = fuelPrice,
                    extraExpenses = extraExpenses,
                    roadFee = roadFee?:0,
                    extraPoints = extraPoints,
                    routeDuration = routeDuration,
                    driverSalary = salary,
                    moneyToPay = moneyToPay,
                    isFinished = true,
                    salaryParameters = salaryParameters,
                    revenue = revenue,
                    profit = profit
                )?.let { routeRepository.updateEditedItem(it) }
                editedRoute.value?.let { routeRepository.updateRouteToDb(it) }
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}