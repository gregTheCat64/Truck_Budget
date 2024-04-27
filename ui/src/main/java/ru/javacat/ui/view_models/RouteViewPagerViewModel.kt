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
class RouteViewPagerViewModel @Inject constructor(
    private val routeRepository: RouteRepository
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()
    val editedRoute = routeRepository.editedRoute

    suspend fun getRouteAndUpdateEditedRoute(id: Long) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO){
            try {
                val editedRoute = routeRepository.getRoute(id)
                if (editedRoute != null) {
                    routeRepository.updateEditedRoute(editedRoute)
                }
                _loadState.emit(LoadState.Success.OK)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun removeRoute(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            try {
                _loadState.emit(LoadState.Loading)
                routeRepository.removeRoute(id)
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }
}