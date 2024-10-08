package ru.javacat.ui.finish_route

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
import ru.javacat.domain.models.RouteDetails
import ru.javacat.domain.models.SalaryCountMethod
import ru.javacat.domain.models.SalaryParameters
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.use_case.CalculateMyDebtUseCase
import ru.javacat.domain.use_case.CalculateProfitUseCase
import ru.javacat.domain.use_case.CalculateRevenueUseCase
import ru.javacat.domain.use_case.CalculateTotalExpensesUseCase
import ru.javacat.ui.LoadState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FinishRouteWithoutDriverViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val calculateTotalExpenses: CalculateTotalExpensesUseCase,
    private val calculateProfitUseCase: CalculateProfitUseCase,
    private val calculateMyDebtUseCase: CalculateMyDebtUseCase,
    private val calculateRevenueUseCase: CalculateRevenueUseCase
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _editedRoute = MutableStateFlow<Route?>(null)
    val editedRoute:MutableStateFlow<Route?>
        get() = _editedRoute

    private val _lastRoute = MutableStateFlow(Route(startDate = LocalDate.now(), salaryParameters = SalaryParameters()))
    val lastRoute: StateFlow<Route>
        get() = _lastRoute.asStateFlow()

    fun getEditedRoute(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val route = routeRepository.getById(routeId)
            if (route != null) {
                _editedRoute.value = route
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
        roadFee: Int,
        routeDuration: Int,
        routeDistance: Int,
        fuelUsedUp: Int,
        fuelPrice: Float,
        revenue: Int,
        profit: Float,
        endDate: LocalDate?,
        totalExpenses: Float
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {

                val routeDetails = RouteDetails(
                    fuelUsedUp, fuelPrice, extraExpenses, roadFee, extraPoints, routeDuration, routeDistance
                )
                _editedRoute.value = editedRoute.value?.copy(
                    prepayment = prepay,
                    routeDetails = routeDetails,
                    driverSalary = 0f,
                    moneyToPay = 0f,
                    isFinished = true,
                    salaryParameters = null,
                    revenue = revenue,
                    profit = profit,
                    endDate = endDate,
                    totalExpenses = totalExpenses
                )
                editedRoute.value?.let { routeRepository.updateRouteToDb(it) }
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun calculateRevenue(route: Route) = calculateRevenueUseCase.invoke(route)

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

}