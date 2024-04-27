package ru.javacat.domain.models

data class Customer (
    override val id: Long = 0L,
    override val name: String,

    val atiNumber: Int? = null,
    val employees: List<Employee>? = null,
    val companyPhone: String? = null,
    val formalAddress: String? = null,
    val postAddress: String? = null,
    val shortName: String? = null,
    override val positionId: Long = 0L,
): BaseNameModel<Long>()
