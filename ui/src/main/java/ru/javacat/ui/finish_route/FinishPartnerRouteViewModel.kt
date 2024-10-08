package ru.javacat.ui.finish_route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class FinishPartnerRouteViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {

    private val _editedRoute = MutableStateFlow<Route?>(null)
    val editedRoute: MutableStateFlow<Route?>
        get() = _editedRoute

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    fun getEditedRoute(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val route = repository.getById(routeId)
            if (route != null) {
                _editedRoute.value = route
            }
        }
    }

    fun saveRoute(profit: Float, revenue: Int, moneyToPay: Float, prepayment: Int, contractorsCost: Int){
        viewModelScope.launch(Dispatchers.IO){
            try {
                _editedRoute.value = editedRoute.value?.copy(
                    routeContractorsSum = contractorsCost,
                    isFinished = true,
                    profit = profit,
                    revenue = revenue,
                    prepayment = prepayment,
                    moneyToPay = moneyToPay,
                    isPaidToContractor = false
                )
                editedRoute.value?.let { repository.updateRouteToDb(it) }
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}