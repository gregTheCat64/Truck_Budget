package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository,

):ViewModel() {

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val editedRoute = routeRepository.editedRoute

    val allRoutes = routeRepository.allRoutes

    private var _prepay: Int? = null
    private var _routeSpending: Int? = null
    private var _routeDuration: Int? = null
    private var _fuelUsedUp: Int? = null
    private var _fuelPrice: Int? = null



    fun saveRoute(isFinished: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
           updateEditedRoute()
            try {
                routeRepository.insertRoute(route = this@RouteViewModel.editedRoute.value)
                if (isFinished){
                    _loadState.emit(LoadState.Success.GoBack)
                } else {
                    _loadState.emit(LoadState.Success.GoForward)
                }


            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }


    fun updateRoute() {
        viewModelScope.launch(Dispatchers.IO){
           updateEditedRoute()
        }
    }

    private suspend fun updateEditedRoute(){
        routeRepository.updateRoute(editedRoute.value.copy(
            prepayment = _prepay,
            routeSpending = _routeSpending,
            routeDuration = _routeDuration,
            fuelUsedUp = _fuelUsedUp,
            fuelPrice = _fuelPrice
        ))
    }

    fun setFieldsData(prepayIn: Int?, routeSpendingIn: Int?, routeDurationIn: Int?, fuelUsedUpIn: Int?, fuelPriceIn: Int?){
        _prepay = prepayIn
        _routeSpending = routeSpendingIn
        _routeDuration = routeDurationIn
        _fuelUsedUp = fuelUsedUpIn
        _fuelPrice = fuelPriceIn
    }
}