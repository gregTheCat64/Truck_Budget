package ru.javacat.domain.models

data class Customer (
    val id: String,
    val companyName: String,
    val atiNumber: Int?,
    val companyPhone: String?,
    val formalAddress: String?,
    val postAddress: String?,
    val shortName: String?
)
