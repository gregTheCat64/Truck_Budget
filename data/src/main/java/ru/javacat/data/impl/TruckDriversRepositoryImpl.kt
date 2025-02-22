package ru.javacat.data.impl

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.TruckDriversDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.data.switchDatabaseModified
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TruckDriversRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: TruckDriversDao
): TruckDriversRepository {

    private val _chosenDriver = MutableStateFlow<TruckDriver?>(null)
    override val chosenDriver: StateFlow<TruckDriver?>
        get() = _chosenDriver.asStateFlow()

    override suspend fun setItem(t: TruckDriver) {
        _chosenDriver.emit(t)
    }

    override suspend fun clearItem() {
        _chosenDriver.emit(null)
    }

    override suspend fun getAll(): List<TruckDriver> {
        return dbQuery {dao.getAll().map { it.toTruckDriverModel() }  }
    }

    override suspend fun getById(id: Long): TruckDriver? {
        return dbQuery { dao.getById(id)?.toTruckDriverModel() }
    }

    override suspend fun getByCompanyId(companyId: Long): List<TruckDriver> {
        return dbQuery { dao.getByCompanyId(companyId).map { it.toTruckDriverModel() } }
    }

    override suspend fun createDefaultTruckDriver(){
        if (getByCompanyId(-1).isEmpty()){
            Log.i("truckDriverRepo", "creating default driver")
            val defaultDriver = TruckDriver(
                0,
                0,
                false,
                -1,
                "Мой",
                surname = "Водитель",
                salaryParameters = null
            )
            dbQuery { dao.insert(defaultDriver.toDb()) }
        }
    }
    override suspend fun createMeAsTruckDriver(){
        if (getById(-1) == null){
            Log.i("truckDriverRepo", "creating me as driver")
            val defaultDriver = TruckDriver(
                -1,
                0,
                false,
                -1,
                "Сам",
                surname = "Зарулем",
                salaryParameters = null
            )
            dbQuery { dao.insert(defaultDriver.toDb()) }
        }
    }



    override suspend fun search(s: String): List<TruckDriver> {
        return dbQuery { dao.searchStaff(s).map { it.toTruckDriverModel() } }
    }

    override suspend fun insert(t: TruckDriver): Long {
        return dbQuery {
            switchDatabaseModified(context, true)
            dao.insert(t.toDb()) }
    }

    override suspend fun updateDriverToDb(truckDriver: TruckDriver) {
        dao.update(truckDriver.toDb())
        switchDatabaseModified(context, true)
    }

    override suspend fun removeById(id: Long) {
        dbQuery {
            switchDatabaseModified(context, true)
            dao.removeStaff(id) }
    }
}