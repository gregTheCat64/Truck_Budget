package ru.javacat.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Customer
import java.time.LocalDate

@Entity(
    tableName = "orders_table",
    foreignKeys = [
        ForeignKey(
            entity = DbRoute::class,
            parentColumns = ["id"],
            childColumns = ["routeId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
    )
data class DbOrder(
    @PrimaryKey
    val routeId: String,
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
