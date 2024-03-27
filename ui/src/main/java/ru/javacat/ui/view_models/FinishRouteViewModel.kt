package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class FinishRouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val editedRoute = routeRepository.editedRoute

    private var _prepay: Int = 0
    private var _routeSpending: Int = 0
    private var _routeDuration: Int = 0
    private var _fuelUsedUp: Int = 0
    private var _fuelPrice: Float = 0f
    private var _payPerDiem: Int = 0
    private var _moneyToPay: Int = 0

    private var _income = 0
    private var _salary: Int? = 0
    private var _netIncome: Float? = 0f

    fun setFieldsData(
        prepayIn: Int,
        routeSpendingIn: Int,
        routeDurationIn: Int,
        fuelUsedUpIn: Int,
        fuelPriceIn: Float,
        payPerDiem: Int,
        salary: Int?
    ) {
        _routeSpending = routeSpendingIn
        _routeDuration = routeDurationIn
        _fuelUsedUp = fuelUsedUpIn
        _fuelPrice = fuelPriceIn
        _prepay = prepayIn
        _payPerDiem = payPerDiem
        _salary = salary
    }


    private suspend fun updateEditedRoute() {
        routeRepository.updateEditedRoute(
            editedRoute.value.copy(
                prepayment = _prepay,
                routeSpending = _routeSpending,
                routeDuration = _routeDuration,
                fuelUsedUp = _fuelUsedUp,
                fuelPrice = _fuelPrice,
                income = _income,
                driverSalary = _salary,
                netIncome = _netIncome,
                payPerDiem = _payPerDiem,
                moneyToPay = _moneyToPay
            )
        )
    }

    fun calculateSalary() {

        viewModelScope.launch(Dispatchers.IO){
            _income = 0
            val orders = editedRoute.value.orderList
            for (i in orders) {
                _income += i.price
            }
            val fuelSpending = _fuelPrice * _fuelUsedUp
            val subsistence = _payPerDiem * _routeDuration

            _salary = ((_income - fuelSpending - subsistence - _routeSpending) / 5).roundToInt()

            _moneyToPay = (_income - _prepay - fuelSpending - _routeSpending - _salary!! - subsistence).toInt()

            updateEditedRoute()
        }
    }

    fun calculateNetIncome() {
        viewModelScope.launch (Dispatchers.IO){
            _netIncome = _income - _salary!! - (_fuelPrice * _fuelUsedUp) - (_payPerDiem * _routeDuration) - _routeSpending
            updateEditedRoute()
        }
    }

    fun saveRoute(){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                routeRepository.insertRoute(editedRoute.value)
                _loadState.emit(LoadState.Success.GoBack)
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}