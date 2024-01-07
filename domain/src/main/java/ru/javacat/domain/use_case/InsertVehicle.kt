package ru.javacat.domain.use_case

import ru.javacat.domain.models.Vehicle
import ru.javacat.domain.repo.Repository

class InsertVehicle(
    private val repository: Repository
) {
    suspend operator fun invoke(vehicle: Vehicle){
        repository.insertTransport(vehicle)
    }
}