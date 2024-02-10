package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Truck

@Entity(tableName = "trucks_table")
data class DbTruck(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val regNumber: String,
    val vin: String?,
    val model: String?,
    val type: String?,
    val yearOfManufacturing: String?,
){
    fun toTruck() = Truck(
        id, regNumber, vin, model, type, yearOfManufacturing
    )
}
