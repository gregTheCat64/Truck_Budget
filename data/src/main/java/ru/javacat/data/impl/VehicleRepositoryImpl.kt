package ru.javacat.data.impl

import ru.javacat.data.db.dao.VehiclesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Vehicle
import ru.javacat.domain.repo.VehicleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepositoryImpl @Inject constructor(
    val vehiclesDao: VehiclesDao
): VehicleRepository {
    override suspend fun insertTransport(vehicle: Vehicle) {
        dbQuery { dbQuery { vehiclesDao.insert(vehicle.toDb()) } }
    }
}