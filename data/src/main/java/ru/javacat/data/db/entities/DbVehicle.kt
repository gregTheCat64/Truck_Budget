package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Vehicle

@Entity(tableName = "vehicles_table")
data class DbVehicle(
    @PrimaryKey
    val id: String,
    val regNumber: String,
    val vin: String?,
    val model: String?,
    val type: String?,
    val yearOfManufacturing: Int,
){
    fun toVehicle() = Vehicle(
        id, regNumber, vin, model, type, yearOfManufacturing
    )
}
