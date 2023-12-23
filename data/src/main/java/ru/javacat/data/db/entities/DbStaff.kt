package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff_table")
data class DbStaff (
    val fullName: String,
    val duty: String,
    @PrimaryKey
    val passportNumber: Int,
    val passportReceivedDate: String,
    val passportReceivedPlace: String,
    val driveLicenseNumber: String?,
    val placeOfRegistration: String?
)