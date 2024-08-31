package ru.javacat.data.db.entities

//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.PrimaryKey
//import ru.javacat.domain.models.SalaryCount
//
//@Entity(
//    tableName = "count_route_table",
//    foreignKeys = [
//        ForeignKey(
//            entity = DbRoute::class,
//            parentColumns = ["id"],
//            childColumns = ["routeId"],
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE
//        )
//    ]
//)
//data class DbCountRoute (
//    @PrimaryKey(autoGenerate = true)
//    val id: Long,
//    val routeId: Long,
//    val prepayment: Int?,
//    val fuelUsedUp: Int? = null,
//    val fuelPrice: Float? = null,
//    val otherExpenses: Int? = null,
//    val payPerDiem: Int? = null,
//    val routeDuration: Int? = null,
//    val driverSalary: Int? = null,
//    val moneyToPay: Int? = null,
//) {
//    fun toModel() = SalaryCount(prepayment, fuelUsedUp,fuelPrice,otherExpenses, payPerDiem, routeDuration, driverSalary, moneyToPay)
//}