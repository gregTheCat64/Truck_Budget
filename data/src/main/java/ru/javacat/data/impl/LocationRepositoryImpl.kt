package ru.javacat.data.impl

import ru.javacat.data.db.dao.LocationsDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Location
import ru.javacat.domain.repo.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationsDao: LocationsDao
): LocationRepository {

    override suspend fun getLocations(): List<Location> {
        return dbQuery { locationsDao.getLocations().map { it.toLocationModel() } }
    }

    override suspend fun searchLocations(search: String): List<Location> {
        return dbQuery { locationsDao.searchLocations(search).map { it.toLocationModel() } }
    }

    override suspend fun insertLocation(location: Location) {
        dbQuery { locationsDao.insertLocation(location.toDb()) }
    }
}