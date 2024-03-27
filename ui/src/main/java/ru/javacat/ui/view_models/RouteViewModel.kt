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
    //val editedOrder = orderRepository.editedOrder


    fun saveRoute(isFinished: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            //updateEditedRoute()
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

    suspend fun getRouteAndUpdateEditedRoute(id: Long) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO){
            try {
                val editedRoute = routeRepository.getRoute(id)?:Route()
                routeRepository.updateEditedRoute(editedRoute)
                _loadState.emit(LoadState.Success.OK)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }


    private suspend fun saveRoute() {
        routeRepository.insertRoute(editedRoute.value)
    }

    fun setRouteFinished(){
        viewModelScope.launch (Dispatchers.IO){
            try {
                routeRepository.updateEditedRoute(editedRoute.value.copy(isFinished = true))
                saveRoute()
                _loadState.emit(LoadState.Success.GoForward)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }


    suspend fun getOrderAndUpdateEditedOrder(id: String) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //saveRoute()
                val editedOrder = orderRepository.getOrderById(id)
                orderRepository.restoringOrder(editedOrder)
                _loadState.emit(LoadState.Success.GoForward)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun clearEditedOrder(){
        viewModelScope.launch(Dispatchers.IO){
            orderRepository.clearCurrentOrder()
        }
    }


}