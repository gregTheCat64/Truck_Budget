package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val editedRoute = routeRepository.editedRoute
    val editedOrder = orderRepository.editedOrder

    private var _prepay: Int? = null
    private var _routeSpending: Int? = null
    private var _routeDuration: Int? = null
    private var _fuelUsedUp: Int? = null
    private var _fuelPrice: Int? = null


    fun saveRoute(isFinished: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            updateEditedRoute()
            try {
                saveRoute()
                if (isFinished) {
                    _loadState.emit(LoadState.Success.GoBack)
                } else {
                    _loadState.emit(LoadState.Success.GoForward)
                }
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }

    fun updateRoute() {
        viewModelScope.launch(Dispatchers.IO) {
            updateEditedRoute()
        }
    }

    private suspend fun saveRoute() {
        routeRepository.insertRoute(editedRoute.value)
    }

    private suspend fun updateEditedRoute() {
        routeRepository.updateRoute(
            editedRoute.value.copy(
                prepayment = _prepay,
                routeSpending = _routeSpending,
                routeDuration = _routeDuration,
                fuelUsedUp = _fuelUsedUp,
                fuelPrice = _fuelPrice
            )
        )
    }

    fun setFieldsData(
        prepayIn: Int?,
        routeSpendingIn: Int?,
        routeDurationIn: Int?,
        fuelUsedUpIn: Int?,
        fuelPriceIn: Int?
    ) {
        _prepay = prepayIn
        _routeSpending = routeSpendingIn
        _routeDuration = routeDurationIn
        _fuelUsedUp = fuelUsedUpIn
        _fuelPrice = fuelPriceIn
    }

    suspend fun getOrderAndUpdateEditedOrder(id: String) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateEditedRoute()
                saveRoute()
                val editedOrder = orderRepository.getOrderById(id)
                orderRepository.updateOrder(editedOrder)
                _loadState.emit(LoadState.Success.GoForward)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }


}