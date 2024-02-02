package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee

@Entity(tableName = "customers_table")
data class DbCustomer(
    @PrimaryKey
    val id: String,
    val companyName: String,
    val atiNumber: Int?,
    val companyPhone: String?,
    val formalAddress: String?,
    val postAddress: String?,
    val shortName: String?
) {
    fun toCustomerModel() = Customer(
        id, companyName, atiNumber, companyPhone,formalAddress, postAddress, shortName
    )
}
