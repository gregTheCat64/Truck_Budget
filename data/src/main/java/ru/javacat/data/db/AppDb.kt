package ru.javacat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.dao.StaffDao
import ru.javacat.data.db.dao.VehiclesDao
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbStaff
import ru.javacat.data.db.entities.DbVehicle


@Database(
    entities = [
        DbCustomer::class,
        DbRoute::class,
        DbEmployee::class,
        DbOrder::class,
        DbStaff::class,
        DbVehicle::class
    ],
    version = 1, exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract val routesDao: RoutesDao
    abstract val customersDao: CustomersDao
    abstract val staffDao: StaffDao
    abstract val vehiclesDao: VehiclesDao
}