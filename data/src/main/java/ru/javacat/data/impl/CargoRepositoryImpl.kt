package ru.javacat.data.impl

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.data.db.dao.CargoDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.CargoName
import ru.javacat.domain.repo.CargoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CargoRepositoryImpl @Inject constructor(
    private val dao: CargoDao
): CargoRepository {

//    override val chosenItem: StateFlow<CargoName?>
//        get() = MutableStateFlow<CargoName?>(null)

    override suspend fun setItem(t: CargoName) {

    }

    override suspend fun clearItem() {

    }

    override suspend fun getAll(): List<CargoName> {
        return dbQuery { dao.getCargos().map { it.toCargoModel() } }
    }

    override suspend fun search(s: String): List<CargoName> {
        return dbQuery {
            dao.searchCargos(s).map { it.toCargoModel() }
        }
    }

    override suspend fun insert(t: CargoName):Long {
        return dbQuery { dao.insertCargo(t.toDb()) }
    }

    override suspend fun getById(id: Long): CargoName {
        return dbQuery { dao.getCargoById(id).toCargoModel() }
    }

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }
}