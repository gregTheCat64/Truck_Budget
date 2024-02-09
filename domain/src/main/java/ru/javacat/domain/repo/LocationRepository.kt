package ru.javacat.domain.repo

import ru.javacat.domain.models.Location

interface LocationRepository {

    suspend fun insertLocation(location: Location)

    suspend fun getLocations(): List<Location>

    suspend fun searchLocations(search: String): List<Location>


}