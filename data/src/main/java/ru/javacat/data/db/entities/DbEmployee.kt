package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Employee

@Entity(tableName = "employees_table",
//    foreignKeys = [ForeignKey(
//        entity = DbCustomer::class,
//        parentColumns = ["id"],
//        childColumns = ["customerId"],
//        onUpdate = ForeignKey.CASCADE,
//        onDelete = ForeignKey.CASCADE
//    )]
    )
data class DbEmployee(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(index = true)
    val customerId: String,
    val name: String,
    val phoneNumber: String,
    val secondNumber: String?,
    val email: String?,
    val comment: String?
) {
    fun toEmployeeModel() = Employee(
        id, customerId,name,phoneNumber,secondNumber,email,comment)

}
