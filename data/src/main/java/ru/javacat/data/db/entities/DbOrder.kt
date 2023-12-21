package ru.javacat.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import ru.javacat.domain.models.Customer
import java.time.LocalDate

@Entity(tableName = "orders")
data class DbOrder(
    val startDate:String,
    val endDate: String,
    val startLocation: String,
    val endLocation: String,
    val price: Int,
    @Embedded
    val customer: Customer,
    val paymentDeadline: String?,
    val sentDocsNumber: String?,
    val docsReceived: String?,
    val isPaid: Boolean
)
