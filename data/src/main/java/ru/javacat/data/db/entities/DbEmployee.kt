package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "employees_table",
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
    @ColumnInfo(index = true)
    val customerAtiNumber: Int,
    val name: String,
    val phoneNumber: String,
    val secondNumber: String?,
    val email: String?
)
