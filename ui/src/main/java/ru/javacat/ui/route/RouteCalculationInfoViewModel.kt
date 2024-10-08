package ru.javacat.ui.route

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
class RouteCalculationInfoViewModel @Inject constructor(
    private val routeRepository: RouteRepository
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _editedRoute = MutableStateFlow<Route?>(null)
    val editedRoute:MutableStateFlow<Route?>
        get() = _editedRoute


    fun updateCurrentRoute(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                _editedRoute.value = routeRepository.getById(id)
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun setPaid(isPaid: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            val route = editedRoute.value?.copy(isPaidToContractor = isPaid)
            if (route != null) {
                routeRepository.updateRouteToDb(route)
            }
        }
    }

    fun removeRoute(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                _loadState.emit(LoadState.Loading)
                routeRepository.removeById(id)
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}