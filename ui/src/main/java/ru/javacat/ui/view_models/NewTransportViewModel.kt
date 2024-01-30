package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Vehicle
import ru.javacat.domain.repo.VehicleRepository
import javax.inject.Inject

@HiltViewModel
class NewTransportViewModel @Inject constructor(
    private val repo: VehicleRepository
): ViewModel() {

    fun insertNewVehicle(vehicle: Vehicle){
        viewModelScope.launch {
            repo.insertTransport(vehicle)
        }
    }
}