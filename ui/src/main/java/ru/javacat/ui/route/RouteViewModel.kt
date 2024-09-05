package ru.javacat.ui.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    routeRepository: RouteRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val editedRoute = routeRepository.editedItem

    fun clearOrder(){
        viewModelScope.launch(Dispatchers.IO){
            orderRepository.clearCurrentOrder()
            //_loadState.emit(LoadState.Success.GoBack)
        }
    }


}