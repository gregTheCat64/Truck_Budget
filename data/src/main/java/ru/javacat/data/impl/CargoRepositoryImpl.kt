package ru.javacat.data.impl

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
    override suspend fun setItem(t: CargoName) {

    }

    override suspend fun getAll(): List<CargoName> {
        return dbQuery { dao.getCargos().map { it.toCargoModel() } }
    }

    override suspend fun search(string: String): List<CargoName> {
        return dbQuery {
            dao.searchCargos(string).map { it.toCargoModel() }
        }
    }

    override suspend fun insert(t: CargoName) {
        dbQuery { dao.insertCargo(t.toDb()) }
    }
}