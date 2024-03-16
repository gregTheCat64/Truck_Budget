package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    @TypeConverters(PointConverter::class)
    val points: List<DbPoint>,
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

class PointConverter {
    @TypeConverter
    fun fromPoints(list: List<DbPoint>): String {
        return Gson().toJson(list)
    }
    @TypeConverter
    fun toPoints(json: String): List<DbPoint> {
        val type = object: TypeToken<List<DbPoint>>() {}.type
        return Gson().fromJson(json, type)
    }
}
