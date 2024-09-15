package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Truck

@Entity(tableName = "trucks_table")
data class DbTruck(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val isHidden: Boolean,
    val companyId: Long,
    val regNumber: String,
    val regionCode: Int,
    val vin: String?,
    val model: String?,
    val type: String?,
    val yearOfManufacturing: String?,
){
    fun toTruck() = Truck(
        id, isHidden, companyId, regNumber,regionCode, vin, model,  yearOfManufacturing
    )
}
