package ru.javacat.data.impl

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.TruckDriversDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TruckDriversRepositoryImpl @Inject constructor(
    private val dao: TruckDriversDao
): TruckDriversRepository {

    private val _chosenDriver = MutableStateFlow<TruckDriver?>(null)
    override val chosenItem: StateFlow<TruckDriver?>
        get() = _chosenDriver.asStateFlow()

    override suspend fun setItem(t: TruckDriver) {
        _chosenDriver.emit(t)
    }

    override suspend fun getAll(): List<TruckDriver> {
        return dbQuery {dao.getAll().map { it.toTruckDriverModel() }  }
    }

    override suspend fun getById(id: String): TruckDriver {
        return dbQuery { dao.getById(id).toTruckDriverModel() }
    }

    override suspend fun search(s: String): List<TruckDriver> {
        return dbQuery { dao.searchStaff(s).map { it.toTruckDriverModel() } }
    }

    override suspend fun insert(t: TruckDriver) {
        dbQuery { dao.insert(t.toDb()) }
    }
}