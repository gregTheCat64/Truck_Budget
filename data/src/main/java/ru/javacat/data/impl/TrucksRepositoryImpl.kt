package ru.javacat.data.impl

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.TrucksDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.data.switchDatabaseModified
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrucksRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    val dao: TrucksDao
) : TrucksRepository {

    private val _chosenTruck = MutableStateFlow<Truck?>(null)
    override val chosenTruck: StateFlow<Truck?>
        get() = _chosenTruck.asStateFlow()

    override suspend fun getAll(): List<Truck> {
        return dbQuery { dao.getAll().map { it.toTruck() } }
    }

    override suspend fun getByCompanyId(companyId: Long): List<Truck> {
        return dbQuery { dao.getByCompanyId(companyId).map { it.toTruck() } }
    }

    override suspend fun getById(id: Long): Truck? {
        return dbQuery { dao.getById(id)?.toTruck() }
    }

    override suspend fun createDefaultTruck(){
        if (getByCompanyId(-1).isEmpty()){
            Log.i("truckRepo", "creating default truck")
            val defaultTruck = Truck(
                0,
                false,
                -1,
                "А000АА",
                regionCode = 0,
            )
            dbQuery { dao.insert(defaultTruck.toDb()) }
        }
    }

    override suspend fun updateDriverToDb(truck: Truck) {
        dbQuery {
            switchDatabaseModified(context, true)
            dao.update(truck.toDb()) }
    }

    override suspend fun removeById(id: Long) {
        dbQuery {
            switchDatabaseModified(context, true)
            dao.remove(id) }
    }

    override suspend fun search(s: String): List<Truck> {
        return dbQuery { dao.searchTrucks(s).map { it.toTruck() } }
    }

    override suspend fun insert(t: Truck): Long {
        return dbQuery {
            switchDatabaseModified(context, true)
            dao.insert(t.toDb()) }
    }

    override suspend fun setItem(t: Truck) {
        _chosenTruck.emit(t)
    }

    override suspend fun clearItem() {
        _chosenTruck.emit(null)
    }
}