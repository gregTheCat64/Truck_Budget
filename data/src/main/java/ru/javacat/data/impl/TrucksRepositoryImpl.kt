package ru.javacat.data.impl

import ru.javacat.data.db.dao.TrucksDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.Vehicle
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrucksRepositoryImpl @Inject constructor(
    val dao: TrucksDao
): TrucksRepository {

    override suspend fun getAllTrucks(): List<Truck> {
        return dbQuery {dao.getAll().map { it.toTruck() }  }
    }

    override suspend fun insertTransport(truck: Truck) {
         dbQuery { dao.insert(truck.toDb()) }
    }
}