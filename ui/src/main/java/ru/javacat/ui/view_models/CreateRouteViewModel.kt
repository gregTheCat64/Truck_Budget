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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreateRouteViewModel @Inject constructor(
    private val repo: RouteRepository
) : ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val editedRoute = repo.editedRoute

    private var _prepay: Int? = null

    private val date = LocalDate.now()

    fun saveNewRoute() {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                    repo.updateEditedRoute(
                        editedRoute.value.copy(prepayment = _prepay)
                    )

                editedRoute.value.let { repo.insertRoute(it) }
                _loadState.emit(LoadState.Success.OK)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun setLastRouteToEditedRoute() {
        viewModelScope.launch(Dispatchers.IO) {
            val lastRoute = repo.lastRoute

            repo.updateEditedRoute(
                    Route(
                        id = lastRoute?.id?.plus(1)?:1,
                        driver = lastRoute?.driver,
                        truck = lastRoute?.truck,
                        trailer = lastRoute?.trailer,
                        prepayment = lastRoute?.prepayment,
                        fuelPrice = lastRoute?.fuelPrice,
                        payPerDiem = lastRoute?.payPerDiem,
                        startDate = date
                    )
                )
        }
    }

    fun setRouteParameters(prepay: Int) {
        _prepay = prepay
    }

}