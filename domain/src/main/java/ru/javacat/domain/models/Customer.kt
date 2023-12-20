package ru.javacat.domain.models

data class Customer (
    val companyName: String,
    val atiNumber: Long,
    val employeeList: List<Employee>?,
    val companyPhone: Long?,
)
