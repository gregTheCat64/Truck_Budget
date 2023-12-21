package ru.javacat.data.db.entities

import androidx.room.Entity

@Entity
data class DbVehicle(
    val regNumber: String,
    val vin: String?,
    val model: String?,
    val type: String?,
    val yearOfManufacturing: Int,
)
