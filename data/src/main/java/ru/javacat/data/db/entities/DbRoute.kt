package ru.javacat.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Contractor
import ru.javacat.domain.models.CountRoute

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
    val countRoute: CountRoute?,

    val contractorsCost: Int = 0,
    val routeExpenses: Int = 0,

    val revenue: Int?,
    val profit: Float?,

    val isFinished: Boolean
)









