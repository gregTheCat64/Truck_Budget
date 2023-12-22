package ru.javacat.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Vehicle
import java.time.LocalDate

@Entity(tableName = "routes_table")
data class DbRoute (
    @PrimaryKey
    val id: String,
    val startDate: String,
    val endDate: String?,
    @Embedded
    val driver: DbMyEmployee,
    @Embedded
    val truck: Vehicle,
    @Embedded
    val trailer: Vehicle?,
    //val orderList: List<Order>,
    val prepayment: Int?,
    val fuelUsedUp: Int?,
    val fuelPrice: Int?,
    val routeExpenses: Int?,
    val routeDuration: Int?,
    val driverSalary: Int?,
    val moneyToPay: Int?,
    val income: Int?,
    val netIncome: Int?,
    val isFinished: Boolean
)




