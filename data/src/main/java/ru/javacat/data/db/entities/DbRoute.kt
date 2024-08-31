package ru.javacat.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.SalaryParameters

@Entity(tableName = "routes_table")
data class DbRoute (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val startDate: String?,
    val endDate: String?,
    val companyId: Long?,
    val driverId: Long?,
    val truckId: Long?,
    val trailerId: Long?,

    @Embedded
    val salaryParameters: SalaryParameters?,

    val prepayment: Int = 0,
    val fuelUsedUp: Int = 0,
    val fuelPrice: Float = 0F,
    val extraExpenses: Int = 0,
    val roadFee: Int = 0,
    val extraPoints: Int = 0,
    val routeDuration: Int = 0,
    val routeDistance: Int = 0,

    val driverSalary: Float = 0F,
    val contractorsCost: Int = 0,
    val totalExpenses: Float = 0F,

    val moneyToPay: Float? = 0F,
    val revenue: Int,
    val profit: Int?,

    val isFinished: Boolean
)









