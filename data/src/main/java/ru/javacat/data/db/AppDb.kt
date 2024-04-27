package ru.javacat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.javacat.data.db.dao.CargoDao
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.dao.EmployeesDao
import ru.javacat.data.db.entities.DbCargo
import ru.javacat.data.db.dao.LocationsDao
import ru.javacat.data.db.dao.OrdersDao
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.dao.StaffDao
import ru.javacat.data.db.dao.TrailersDao
import ru.javacat.data.db.dao.TrucksDao
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.data.db.entities.DbLocation
import ru.javacat.data.db.entities.DbOrder

import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbStaff
import ru.javacat.data.db.entities.DbTrailer
import ru.javacat.data.db.entities.DbTruck
import ru.javacat.data.db.entities.PointConverter


@Database(
    entities = [
        DbCustomer::class,
        DbRoute::class,
        DbEmployee::class,
        DbOrder::class,
        DbStaff::class,
        DbTruck::class,
        DbTrailer::class,
        DbLocation::class,
        DbCargo::class
    ],
    version = 1, exportSchema = false
)
@TypeConverters(PointConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract val routesDao: RoutesDao
    abstract val customersDao: CustomersDao
    abstract val employeesDao: EmployeesDao
    abstract val staffDao: StaffDao
    abstract val trucksDao: TrucksDao
    abstract val trailersDao: TrailersDao
    abstract val locationsDao: LocationsDao
    abstract val cargoDao: CargoDao
    abstract val ordersDao: OrdersDao
}