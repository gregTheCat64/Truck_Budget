package ru.javacat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.javacat.data.db.dao.CargoDao
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.dao.ManagersDao
import ru.javacat.data.db.entities.DbCargo
import ru.javacat.data.db.dao.LocationsDao
import ru.javacat.data.db.dao.OrdersDao
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.dao.TruckDriversDao
import ru.javacat.data.db.dao.TrailersDao
import ru.javacat.data.db.dao.TrucksDao
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbManager
import ru.javacat.data.db.entities.DbLocation
import ru.javacat.data.db.entities.DbOrder

import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbTruckDriver
import ru.javacat.data.db.entities.DbTrailer
import ru.javacat.data.db.entities.DbTruck
import ru.javacat.data.db.entities.PointConverter


@Database(
    entities = [
        DbCustomer::class,
        DbRoute::class,
        DbManager::class,
        DbOrder::class,
        DbTruckDriver::class,
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
    abstract val managersDao: ManagersDao
    abstract val truckDriversDao: TruckDriversDao
    abstract val trucksDao: TrucksDao
    abstract val trailersDao: TrailersDao
    abstract val locationsDao: LocationsDao
    abstract val cargoDao: CargoDao
    abstract val ordersDao: OrdersDao
}