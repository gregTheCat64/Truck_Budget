package ru.javacat.domain.use_case

import ru.javacat.domain.models.Vehicle
import ru.javacat.domain.repo.RouteRepository

class InsertVehicle(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(vehicle: Vehicle){
        repository.insertTransport(vehicle)
    }
}