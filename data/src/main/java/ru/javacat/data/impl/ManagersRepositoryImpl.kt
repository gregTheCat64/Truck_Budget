package ru.javacat.data.impl

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.ManagersDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Manager
import ru.javacat.domain.repo.ManagersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagersRepositoryImpl @Inject constructor(
    private val dao: ManagersDao
): ManagersRepository {

    private val _chosenManager = MutableStateFlow<Manager?>(null)
    override val chosenItem: StateFlow<Manager?>
        get() = _chosenManager.asStateFlow()

    override suspend fun getAll(): List<Manager> {
        return dbQuery { dao.getAll().map {
            it.toManagerModel() }
        }
    }

    override suspend fun search(s: String): List<Manager> {
        return dbQuery { dao.searchManagers(s).map { it.toManagerModel() } }
    }

    override suspend fun insert(t: Manager) {
        dbQuery { dao.insertManager(t.toDb()) }
    }

    override suspend fun setItem(t: Manager) {
        _chosenManager.emit(t)
    }

    override suspend fun getManagersByCustomerId(customerId: Long): List<Manager> {
        val result = dbQuery {
            dao.getManagersByCustomerId(customerId).map {
                it.toManagerModel()
            } }
        return result
    }

    override suspend fun getById(id: Long): Manager {
        return dbQuery { dao.getById(id).toManagerModel() }
    }
}