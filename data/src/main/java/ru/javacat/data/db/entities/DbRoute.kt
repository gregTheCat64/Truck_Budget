package ru.javacat.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.RouteDetails
import ru.javacat.domain.models.SalaryParameters

@Entity(tableName = "routes_table")
data class DbRoute (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val startDate: String?,
    val endDate: String?,
    val contractorId: Long?,
    val driverId: Long?,
    val truckId: Long?,
    val trailerId: Long?,

    @Embedded
    val routeDetails: RouteDetails?,
    @Embedded
    val salaryParameters: SalaryParameters?,

    val prepayment: Int = 0,
    val driverSalary: Float? = null,
    val routeContractorsSum: Int? = null,
    val totalExpenses: Float? = null,

    val moneyToPay: Float? = null,
    val isPaidToContractor: Boolean = false,
    val revenue: Int? = null,
    val profit: Float? = null,

    val isFinished: Boolean
)









