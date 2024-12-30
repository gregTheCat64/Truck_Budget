package ru.javacat.data.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.ManagersDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.data.switchDatabaseModified
import ru.javacat.domain.models.Manager
import ru.javacat.domain.repo.ManagersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManagersRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: ManagersDao
): ManagersRepository {

    override suspend fun getAll(): List<Manager> {
        return dbQuery { dao.getAll().map {
            it.toManagerModel() }
        }
    }

    override suspend fun search(s: String): List<Manager> {
        return dbQuery { dao.searchManagers(s).map { it.toManagerModel() } }
    }

    override suspend fun insert(t: Manager): Long {
        return dbQuery {
            switchDatabaseModified(context, true)
            dao.insertManager(t.toDb()) }
    }

    override suspend fun setItem(t: Manager) {
        //_chosenManager.emit(t)
    }

    override suspend fun clearItem() {
        //_chosenManager.emit(null)
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

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }
}