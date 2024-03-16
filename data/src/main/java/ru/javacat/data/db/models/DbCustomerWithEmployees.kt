package ru.javacat.data.db.models

import androidx.room.Relation
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.CustomerWithEmployees

data class DbCustomerWithEmployees(
    val id: String,
    val companyName: String,
    val atiNumber: Int?,
    val companyPhone: Long?,
    val formalAddress: String,
    val postAddress: String,
    val shortName: String,
    val positionId: Int,

    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val employees: List<DbEmployee>
) {
    fun toCustomerModel() = CustomerWithEmployees(
        id,
        companyName,
        atiNumber,
        employees.map { it.toEmployeeModel() },
        companyPhone,
        formalAddress, postAddress, shortName
    )
}
