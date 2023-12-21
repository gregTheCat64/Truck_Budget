package ru.javacat.data.db.entities

import androidx.room.Entity
import ru.javacat.domain.models.Employee

@Entity
data class DbCustomer(
val companyName: String,
val atiNumber: Int,
//val employeeList: List<Employee>?,
val companyPhone: String?,
)
