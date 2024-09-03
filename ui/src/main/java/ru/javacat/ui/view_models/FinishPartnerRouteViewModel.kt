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

@HiltViewModel
class FinishPartnerRouteViewModel @Inject constructor(
    private val repository: RouteRepository
): ViewModel() {
    val editedRoute = repository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    fun getEditedRoute(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val route = repository.getById(routeId)
            if (route != null) {
                repository.updateEditedItem(route)
            }
        }
    }

    fun saveRoute(profit: Float, revenue: Int, moneyToPay: Float, prepayment: Int){
        viewModelScope.launch(Dispatchers.IO){
            try {
                editedRoute.value?.copy(
                    isFinished = true,
                    profit = profit,
                    revenue = revenue,
                    prepayment = prepayment,
                    moneyToPay = moneyToPay,
                )?.let { repository.updateEditedItem(it) }
                editedRoute.value?.let { repository.updateRouteToDb(it) }
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}