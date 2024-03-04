package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.OrderStatus
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Staff
import ru.javacat.domain.models.Vehicle
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
    val id: String,
    @ColumnInfo(index = true)
    val routeId: Long,
    //val points: List<Point>,
    val driverId: Long,
    val truckId: Long,
    val trailerId: Long?,
    val price: Int,
    val customerId: Long,
    val cargoWeight: Int?,
    val cargoVolume: Int?,
    val cargoName: String?,
    val extraConditions: String?,
    val daysToPay: Int?,
    val paymentDeadline: String?,
    val sentDocsNumber: String?,
    val docsReceived: String?,
    @TypeConverters(StatusConverter::class)
    val status: OrderStatus
)

class StatusConverter {
    @TypeConverter
    fun toStatus(value: String) = enumValueOf<OrderStatus>(value)
    @TypeConverter
    fun fromStatus(value: OrderStatus) = value.name
}
