package ru.javacat.domain.models

import java.time.LocalDate

data class Order (
    val id: Long = 0L,
    val routeId: Long = 0L,
    val points: List<Point> = emptyList(),
    val date: LocalDate,
    val price: Int? = null,
    val payType: PayType = PayType.WITHOUT_FEE,
    val contractorPrice: Int? = null,
    val payTypeToContractor: PayType? = null,
    val commission: Int? = null,
    val customer: Company? = null,
    val manager: Manager? = null,
    val contractor: Contractor? = null,
    val cargo: Cargo? = null,
    val extraConditions: String? = null,
    val daysToPay: Int? = null,
    val daysToPayToContractor: Int? = null,
    val paymentDeadline: LocalDate? = null,
    val sentDocsNumber: String? = null,
    val docsReceived: LocalDate? = null,
    val isPaidByCustomer: Boolean = false,
    val isPaidToContractor: Boolean = false
)

enum class PayType{
    CASH, CARD, WITHOUT_FEE, WITH_FEE
}

//enum class OrderStatus(){
//    IN_PROGRESS, WAITING_FOR_PAYMENT, PAID
//}
