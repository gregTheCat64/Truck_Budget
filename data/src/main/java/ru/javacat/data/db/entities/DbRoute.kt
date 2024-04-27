package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes_table")
data class DbRoute (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val startDate: String?,
    val endDate: String?,
    val driverId: Long,
    val truckId: Long?,
    val trailerId: Long?,
    //val orderList: List<Order>,
    val prepayment: Int,
    val fuelUsedUp: Int?,
    val fuelPrice: Float?,
    val routeExpenses: Int,
    val payPerDiem: Int?,
    val routeDuration: Int,
    val driverSalary: Int?,
    val moneyToPay: Int?,
    val income: Int?,
    val netIncome: Float?,
    val isFinished: Boolean
)






