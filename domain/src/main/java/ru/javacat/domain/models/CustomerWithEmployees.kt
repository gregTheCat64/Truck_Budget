package ru.javacat.domain.models

data class CustomerWithEmployees (
    val companyName: String,
    val atiNumber: Int,
    val employeeList: List<Employee>?,
    val companyPhone: Long?,
)