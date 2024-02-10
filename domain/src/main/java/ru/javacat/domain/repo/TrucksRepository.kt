package ru.javacat.domain.repo

import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.Vehicle

interface TrucksRepository {


    suspend fun getAllTrucks(): List<Truck>

    suspend fun insertTransport(truck: Truck)
}