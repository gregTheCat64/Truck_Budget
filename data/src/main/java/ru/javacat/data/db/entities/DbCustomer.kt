package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Employee

@Entity(tableName = "customers_table")
data class DbCustomer(
    val companyName: String,
    @PrimaryKey
    val atiNumber: Int,
//val employeeList: List<Employee>?,
    val companyPhone: String?,
)
