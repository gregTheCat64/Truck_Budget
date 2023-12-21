package ru.javacat.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import ru.javacat.domain.models.Vehicle
import java.time.LocalDate

@Entity
data class DbRoute (
    val startDate: String,
    val endDate: String?,
    @Embedded
    val driver: DbTruckDriver,
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

@Entity
data class DbTruckDriver (
    val fullName: String,
    val idNumber: Int,
    val idReceivedDate: String,
    val idReceivedPlace: String,
    val registeredAt: String
)


