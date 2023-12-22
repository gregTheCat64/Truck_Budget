package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "employees",
    foreignKeys = [ForeignKey(
        entity = DbCustomer::class,
        parentColumns = ["atiNumber"],
        childColumns = ["customerAtiNumber"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
    )
data class DbEmployee(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val customerAtiNumber: Int,
    val name: String,
    val phoneNumber: String,
    val secondNumber: String?,
    val email: String?
)
