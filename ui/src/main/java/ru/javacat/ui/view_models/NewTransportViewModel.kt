package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.javacat.domain.models.Vehicle
import ru.javacat.domain.repo.VehicleRepository
import javax.inject.Inject

@HiltViewModel
class NewTransportViewModel @Inject constructor(
    private val repo: VehicleRepository
): ViewModel() {

    suspend fun insertNewVehicle(vehicle: Vehicle){
        repo.insertTransport(vehicle)
    }
}