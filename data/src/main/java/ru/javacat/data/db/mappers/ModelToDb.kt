package ru.javacat.data.db.mappers

import ru.javacat.data.db.dao.DbCargo
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.data.db.entities.DbLocation
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbPoint
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbStaff
import ru.javacat.data.db.entities.DbTrailer
import ru.javacat.data.db.entities.DbTruck
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.OrderStatus
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.Staff
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck

fun Route.toDb() = DbRoute(
    id,
    startDate?.toString() ,
    endDate?.toString(),
    driver?.id?:0,
    truck?.id?:0,
    trailer?.id?:0,
    prepayment,
    fuelUsedUp,
    fuelPrice,
    routeSpending,
    routeDuration,
    driverSalary,
    moneyToPay,
    income,
    netIncome,
    isFinished
)

fun Order.toDb() = DbOrder(
    id,
    routeId,
    driverId,
    truckId,
    trailerId,
    price,
    customer?.id?:0,
    cargoWeight,
    cargoVolume,
    cargoName,
    extraConditions,
    daysToPay,
    paymentDeadline?.toString(),
    sentDocsNumber,
    docsReceived?.toString(),
    status?:OrderStatus.IN_PROGRESS
)

fun Point.toDb(order: Order) = DbPoint(
    id, order.id, location.toDb(), arrivalDate.toString()
)

fun Truck.toDb() = DbTruck(
    id, regNumber, vin, model, type, yearOfManufacturing
)

fun Trailer.toDb() = DbTrailer(
    id, regNumber, vin, model, type, yearOfManufacturing
)

fun Staff.toDb() = DbStaff(
    id,
    fullName,
    passportSerial,
    passportNumber,
    passportReceivedDate,
    passportReceivedPlace,
    driveLicenseNumber,
    placeOfRegistration,
    phoneNumber
)

fun Location.toDb() = DbLocation(
    id, name, positionId
)

fun Customer.toDb() = DbCustomer(
    id, name,positionId, atiNumber, companyPhone, formalAddress, postAddress, shortName
)

fun Employee.toDb() = DbEmployee(
    id, customerId, name, phoneNumber, secondNumber, email, comment
)

fun Cargo.toDb() = DbCargo(id, name, positionId)