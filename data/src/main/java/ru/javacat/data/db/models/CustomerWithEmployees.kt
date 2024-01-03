package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.domain.models.Customer

data class CustomerWithEmployees(
    @Embedded
    val customer: DbCustomer,

    @Relation(
        parentColumn = "atiNumber",
        entityColumn = "customerAtiNumber"
    )
    val employees: List<DbEmployee>
) {
    fun toCustomerModel() = Customer(
        customer.companyName,
        customer.atiNumber,
        employees.map { it.toEmployeeModel() },
        customer.companyPhone
    )
}
