package ru.javacat.ui.view_models

import android.util.Log
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
import ru.javacat.domain.models.CountRoute
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.use_case.CalculateProfitUseCase
import ru.javacat.domain.use_case.CalculateTruckDriverSalaryUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class FinishRouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository
) : ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val editedRoute = routeRepository.editedItem
    private val _lastRoute = MutableStateFlow(Route())
    val lastRoute: StateFlow<Route>
        get() = _lastRoute.asStateFlow()

    //val lastRoute = routeRepository.lastRoute

//    private var _prepay: Int = 0
//    private var _otherExpenses: Int = 0
//    private var _routeDuration: Int = 0
//    private var _fuelUsedUp: Int = 0
//    private var _fuelPrice: Float = 0f
//    private var _payPerDiem: Int = 0
//    private var _moneyToPay: Int = 0
//
//    private var _revenue = 0
//    private var _salary: Int? = 0
//    private var _profit: Float? = 0f

//    fun setFieldsData(
//        prepayIn: Int,
//        routeSpendingIn: Int,
//        routeDurationIn: Int,
//        fuelUsedUpIn: Int,
//        fuelPriceIn: Float,
//        payPerDiem: Int,
//        salary: Int?
//    ) {
//        _otherExpenses = routeSpendingIn
//        _routeDuration = routeDurationIn
//        _fuelUsedUp = fuelUsedUpIn
//        _fuelPrice = fuelPriceIn
//        _prepay = prepayIn
//        _payPerDiem = payPerDiem
//        _salary = salary
//    }


    private suspend fun updateEditedRoute(
        _prepay: Int,
        _otherExpenses: Int,
        _routeDuration: Int,
        _fuelUsedUp: Int,
        _fuelPrice: Float,
        _salary: Int,
        _payPerDiem: Int,
        _moneyToPay: Int,
    ) {
        var countRoute: CountRoute? = null

        countRoute =
            CountRoute(
                prepayment = _prepay,
                otherExpenses = _otherExpenses,
                routeDuration = _routeDuration,
                fuelUsedUp = _fuelUsedUp,
                fuelPrice = _fuelPrice,
                driverSalary = _salary,
                payPerDiem = _payPerDiem,
                moneyToPay = _moneyToPay,
            )

        editedRoute.value.copy(
            countRoute = countRoute
        ).let {
            routeRepository.updateEditedItem(
                it
            )
        }
    }

    fun getEditedRoute(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val route = routeRepository.getById(routeId)
            if (route != null) {
                routeRepository.updateEditedItem(route)
            }
        }
    }

    fun getLastRoute() {
        viewModelScope.launch(Dispatchers.IO){
            routeRepository.lastRoute?.let { _lastRoute.emit(it) }
        }
    }


    fun saveRoute(prepay: Int,
                  otherExpenses: Int,
                  routeDuration: Int,
                  fuelUsedUp: Int,
                  fuelPrice: Float,
                  salary: Int,
                  payPerDiem: Int,
                  moneyToPay: Int,
                  revenue: Int,
                  profit: Float
                  ) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val countRoute = CountRoute(prepay,fuelUsedUp, fuelPrice, otherExpenses,
                    payPerDiem, routeDuration, salary, moneyToPay)
                routeRepository.updateEditedItem(editedRoute.value.copy(
                    isFinished = true,
                    countRoute = countRoute,
                    revenue = revenue,
                    profit = profit
                    ))
                routeRepository.updateRoute(editedRoute.value)
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}