package ru.javacat.ui.route

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.use_case.ClearEditedOrderUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class RouteViewPagerViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val clearEditedOrderUseCase: ClearEditedOrderUseCase
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()
    val editedRoute = routeRepository.editedItem

    suspend fun getRouteAndUpdateEditedRoute(id: Long) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO){
            try {
                val editedRoute = routeRepository.getById(id)
                Log.i("RouteVP_VM", "getRouteAndUpdateEditedRoute: $editedRoute")
                if (editedRoute != null) {
                    routeRepository.updateEditedItem(editedRoute)
                }
                _loadState.emit(LoadState.Success.OK)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun clearEditedOrder(){
        viewModelScope.launch(Dispatchers.IO){
            clearEditedOrderUseCase.invoke()
        }
    }

    suspend fun removeRoute(id: Long){
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