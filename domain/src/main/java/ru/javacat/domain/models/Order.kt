package ru.javacat.domain.models

import java.time.LocalDate

abstract class BaseOrder(){
    abstract val id: Long
    abstract val points: List<Point>
    abstract val date: LocalDate
    abstract val price: Int?
    abstract val customer: Company?
    abstract val manager: Manager?
    abstract val driver: TruckDriver?
    abstract val truck: Truck?
    abstract val trailer: Trailer?
    abstract val cargo: Cargo?
    abstract val extraConditions: String?
    abstract val daysToPay: Int?
    abstract val paymentDeadline: LocalDate?
    abstract val sentDocsNumber: String?
    abstract val docsReceived: LocalDate?
    abstract val isPaidByCustomer: Boolean
}

//data class Order(
//    override val id: Long = 0L,
//    val routeId: Long = 0L,
//    override val points: List<Point> = emptyList(),
//    override val date: LocalDate,
//    override val price: Int = 0,
//    override val customer: Partner? = null,
//    override val manager: Manager? = null,
//    override val driver: TruckDriver? = null,
//    override val truck: Truck? = null,
//    override val trailer: Trailer? = null,
//    override val cargo: Cargo? = null,
//    override val extraConditions: String? = null,
//    override val daysToPay: Int? = null,
//    override val paymentDeadline: LocalDate? = null,
//    override val sentDocsNumber: String? = null,
//    override val docsReceived: LocalDate? = null,
//    override val isPaidByCustomer: Boolean = false
//): BaseOrder()

data class Order (
    val id: Long = 0L,
    val routeId: Long = 0L,
    val points: List<Point> = emptyList(),
    val date: LocalDate,
    val price: Int? = null,
    val contractorPrice: Int? = null,
    val commission: Int? = null,
    val customer: Company? = null,
    val manager: Manager? = null,
    val contractor: Contractor? = null,
    val cargo: Cargo? = null,
    val extraConditions: String? = null,
    val daysToPay: Int? = null,
    val paymentDeadline: LocalDate? = null,
    val sentDocsNumber: String? = null,
    val docsReceived: LocalDate? = null,
    val isPaidByCustomer: Boolean = false,
    val isPaidToContractor: Boolean = false
)


//enum class OrderStatus(){
//    IN_PROGRESS, WAITING_FOR_PAYMENT, PAID
//}
