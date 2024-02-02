package ru.javacat.domain.models

data class CustomerWithEmployees (
    val id: String,
    val companyName: String,
    val atiNumber: Int?,
    val employeeList: List<Employee>?,
    val companyPhone: Long?,
    val formalAddress: String?,
    val postAddress: String?,
    val shortName: String?
)