package ru.javacat.domain.models

import java.time.LocalDate

abstract class BaseOrder(){
    abstract val id: Long
    abstract val points: List<Point>
    abstract val date: LocalDate
    abstract val price: Int
    abstract val customer: Partner?
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

data class Order(
    override val id: Long = 0L,
    val routeId: Long = 0L,
    override val points: List<Point> = emptyList(),
    override val date: LocalDate,
    override val price: Int = 0,
    override val customer: Partner? = null,
    override val manager: Manager? = null,
    override val driver: TruckDriver? = null,
    override val truck: Truck? = null,
    override val trailer: Trailer? = null,
    override val cargo: Cargo? = null,
    override val extraConditions: String? = null,
    override val daysToPay: Int? = null,
    override val paymentDeadline: LocalDate? = null,
    override val sentDocsNumber: String? = null,
    override val docsReceived: LocalDate? = null,
    override val isPaidByCustomer: Boolean = false
): BaseOrder()

data class PartnerOrder (
    override val id: Long = 0L,
    override val points: List<Point> = emptyList(),
    override val date: LocalDate,
    override val price: Int = 0,
    val contractorPrice: Int = 0,
    val income: Int = 0,
    override val customer: Partner? = null,
    override val manager: Manager? = null,
    val contractor: Partner,
    override val driver: TruckDriver? = null,
    override val truck: Truck? = null,
    override val trailer: Trailer? = null,
    override val cargo: Cargo? = null,
    override val extraConditions: String? = null,
    override val daysToPay: Int? = null,
    override val paymentDeadline: LocalDate? = null,
    override val sentDocsNumber: String? = null,
    override val docsReceived: LocalDate? = null,
    override val isPaidByCustomer: Boolean = false,
    val isPaidToContractor: Boolean = false
): BaseOrder()

//enum class OrderStatus(){
//    IN_PROGRESS, WAITING_FOR_PAYMENT, PAID
//}
