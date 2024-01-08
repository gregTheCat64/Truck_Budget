package ru.javacat.domain.repo

import ru.javacat.domain.models.Vehicle

interface VehicleRepository {

    suspend fun insertTransport(vehicle: Vehicle)
}