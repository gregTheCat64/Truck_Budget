package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.RouteRepository
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository,

):ViewModel() {
    val editedRoute = routeRepository.editedRoute

    private var prepay: Int? = null
    private var routeSpending: Int? = null
    private var routeDuration: Int? = null
    private var fuelUsedUp: Int? = null
    private var fuelPrice: Int? = null


    fun saveRoute(){
        viewModelScope.launch(Dispatchers.IO){
           updateEditedRoute()
            routeRepository.insertRoute(route = this@RouteViewModel.editedRoute.value)
        }
    }

    fun updateRoute() {
        viewModelScope.launch(Dispatchers.IO){
           updateEditedRoute()
        }
    }

    private suspend fun updateEditedRoute(){
        routeRepository.updateRoute(editedRoute.value.copy(
            prepayment = prepay,
            routeSpending = routeSpending,
            routeDuration = routeDuration,
            fuelUsedUp = fuelUsedUp,
            fuelPrice = fuelPrice
        ))
    }

    fun setFieldsData(prepayIn: Int?, routeSpendingIn: Int?, routeDurationIn: Int?, fuelUsedUpIn: Int?, fuelPriceIn: Int?){
        prepay = prepayIn
        routeSpending = routeSpendingIn
        routeDuration = routeDurationIn
        fuelUsedUp = fuelUsedUpIn
        fuelPrice = fuelPriceIn
    }
}