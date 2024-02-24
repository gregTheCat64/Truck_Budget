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
class RouteListViewModel @Inject constructor(
    private val repo: RouteRepository
): ViewModel() {

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val allRoutes = repo.allRoutes

    suspend fun insertNewRoute(route: Route){
        _loadState.emit(LoadState.Loading)
        println("inserting $route")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.insertRoute(route)
                println("result - $result")
                //_newRouteId.emit(result)
                _loadState.emit(LoadState.Success)

            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun getRouteAndUpdateEditedRoute(id: Long) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO){
            try {
                val editedRoute = repo.getRoute(id)
                repo.updateRoute(editedRoute)
                _loadState.emit(LoadState.Success)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }


    suspend fun removeRoute(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            repo.removeRoute(id)
        }
    }

}