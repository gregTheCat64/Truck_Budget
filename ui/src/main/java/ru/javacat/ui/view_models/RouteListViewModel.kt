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

    val allRoutes = repo.items

    fun getAllRoutes(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAll()
        }
    }


    suspend fun removeRoute(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            repo.removeById(id)
        }
    }

}