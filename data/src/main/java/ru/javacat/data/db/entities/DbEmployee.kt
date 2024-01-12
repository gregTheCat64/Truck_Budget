package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Employee

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
    val phoneNumber: Long,
    val secondNumber: Long?,
    val email: String?
) {
    fun toEmployeeModel() = Employee(
        id, customerAtiNumber,name,phoneNumber,secondNumber,email)

}
