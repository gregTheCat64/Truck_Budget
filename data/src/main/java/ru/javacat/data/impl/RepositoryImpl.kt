package ru.javacat.data.impl

import androidx.room.Dao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.Repository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val routesDao: RoutesDao
):Repository {
    override val allRoutes: Flow<List<Route?>>
        get() = routesDao.getAllRoutes().map { list -> list.map { it.toRouteModel() } }

    override val allOrders: Flow<List<Order?>>
        get() = routesDao.getAllOrders().map {list-> list.map { it.toOrderModel() } }
}