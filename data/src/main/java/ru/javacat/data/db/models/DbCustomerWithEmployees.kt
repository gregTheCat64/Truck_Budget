package ru.javacat.data.db.models

import androidx.room.Relation
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.CustomerWithEmployees

data class DbCustomerWithEmployees(
    val companyName: String,
    val atiNumber: Int,
    val companyPhone: Long?,

    @Relation(
        parentColumn = "atiNumber",
        entityColumn = "customerAtiNumber"
    )
    val employees: List<DbEmployee>
) {
    fun toCustomerModel() = CustomerWithEmployees(
        companyName,
        atiNumber,
        employees.map { it.toEmployeeModel() },
        companyPhone
    )
}
