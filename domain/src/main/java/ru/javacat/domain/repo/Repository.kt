package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.Staff
import ru.javacat.domain.models.Vehicle
import java.sql.Driver

interface Repository {
    val allRoutes: Flow<List<Route?>>
    val allOrders: Flow<List<Order?>>

    suspend fun insertOrder(route:Route, order: Order)

    suspend fun insertRoute(route: Route)

    suspend fun insertLocation(location: Location)

    suspend fun insertTransport(vehicle: Vehicle)

    suspend fun insertDriver(driver: Staff)

    suspend fun insertCustomer(customer: Customer)

    suspend fun insertEmployee(employee: Employee)
}