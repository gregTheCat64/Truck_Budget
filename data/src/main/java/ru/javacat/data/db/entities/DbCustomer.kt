package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers_table")
data class DbCustomer(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val companyName: String,

    val atiNumber: Int?,
    val companyPhone: String?,
    val formalAddress: String?,
    val postAddress: String?,
    val shortName: String?,
    val positionId: Long,
) {
//    fun toCustomerModel() = Customer(
//        id, companyName, atiNumber, companyPhone,formalAddress, postAddress, shortName, positionId
//    )
}
