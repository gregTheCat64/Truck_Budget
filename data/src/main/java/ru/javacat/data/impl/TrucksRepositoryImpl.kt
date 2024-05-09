package ru.javacat.data.impl

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.TrucksDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrucksRepositoryImpl @Inject constructor(
    val dao: TrucksDao
) : TrucksRepository {

    private val _chosenTruck = MutableStateFlow<Truck?>(null)
    override val chosenItem: StateFlow<Truck?>
        get() = _chosenTruck.asStateFlow()

    override suspend fun getAll(): List<Truck> {
        return dbQuery { dao.getAll().map { it.toTruck() } }
    }

    override suspend fun getById(id: Long): Truck {
        return dbQuery { dao.getById(id).toTruck() }
    }

    override suspend fun search(s: String): List<Truck> {
        return dbQuery { dao.searchTrucks(s).map { it.toTruck() } }
    }

    override suspend fun insert(t: Truck) {
        dbQuery { dao.insert(t.toDb()) }
    }

    override suspend fun setItem(t: Truck) {
        _chosenTruck.emit(t)
    }
}