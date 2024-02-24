package ru.javacat.data.impl

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.StaffDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Staff
import ru.javacat.domain.repo.StaffRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepositoryImpl @Inject constructor(
    private val dao: StaffDao
): StaffRepository {

    private val _chosenDriver = MutableStateFlow<Staff?>(null)
    override val chosenDriver: StateFlow<Staff?>
        get() = _chosenDriver.asStateFlow()

    override suspend fun setItem(t: Staff) {
        _chosenDriver.emit(t)
    }

    override suspend fun getAll(): List<Staff> {
        return dbQuery {dao.getAll().map { it.toStaff() }  }
    }

    override suspend fun search(s: String): List<Staff> {
        return dbQuery { dao.searchStaff(s).map { it.toStaff() } }
    }

    override suspend fun insert(t: Staff) {
        dbQuery { dao.insert(t.toDb()) }
    }
}