package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies_table")
data class DbCompany(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val companyName: String,
    val isHidden: Boolean = false,
    val atiNumber: Int?,
    val companyPhone: String?,
    val formalAddress: String?,
    val postAddress: String?,
    val shortName: String?,
    val positionId: Long,
) {
//    fun toCustomerModel() = Partner(
//        id, companyName, atiNumber, companyPhone,formalAddress, postAddress, shortName, positionId
//    )
}
