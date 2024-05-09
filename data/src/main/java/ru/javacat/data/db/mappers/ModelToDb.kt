package ru.javacat.data.db.mappers

import ru.javacat.data.db.entities.DbCargo
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbManager
import ru.javacat.data.db.entities.DbLocation
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbPoint
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbTrailer
import ru.javacat.data.db.entities.DbTruck
import ru.javacat.data.db.entities.DbTruckDriver
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.CargoName
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Manager
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.TruckDriver

fun Route.toDb() = DbRoute(
    id,
    startDate?.toString() ,
    endDate?.toString(),
    driver?.id?:0,
    truck?.id?:0,
    trailer?.id?:0,
    prepayment?:0,
    fuelUsedUp,
    fuelPrice,
    routeSpending,
    payPerDiem,
    routeDuration?:0,
    driverSalary,
    moneyToPay,
    income,
    netIncome,
    isFinished
)

fun Order.toDb() = DbOrder(
    id,
    routeId?:0L,
    points.map { it.toDb() },
    points[0].arrivalDate.toString(),
    price?:0,
    customer?.id?:0,
    manager?.id,
    driver?.id?: 0,
    truck?.id?: 0,
    trailer?.id,
    cargo?: Cargo(20, 82, "ТНП", true, false,false),
    extraConditions,
    daysToPay,
    paymentDeadline?.toString(),
    sentDocsNumber,
    docsReceived?.toString(),
    isPaid
)

fun Point.toDb() = DbPoint(
    id,  location, arrivalDate.toString()
)

fun Truck.toDb() = DbTruck(
    id, regNumber, regionCode, vin, model, type, yearOfManufacturing
)

fun Trailer.toDb() = DbTrailer(
    id, regNumber, regionCode, vin, model, type, yearOfManufacturing
)

fun TruckDriver.toDb() = DbTruckDriver(
    id,
    positionId,
    customerId,
    firstName,
    middleName,
    surname,
    passportNumber,
    passportReceivedDate,
    passportReceivedPlace,
    driveLicenseNumber,
    placeOfRegistration,
    phoneNumber,
    secondNumber,
    comment
)

fun Location.toDb() = DbLocation(
    id, name, positionId
)

fun Customer.toDb() = DbCustomer(
    id, name, atiNumber, companyPhone, formalAddress, postAddress, shortName,positionId,
)

fun Manager.toDb() = DbManager(
    id,
    positionId,
    customerId,
    firstName,
    middleName,
    surname,
    passportNumber,
    passportReceivedDate,
    passportReceivedPlace,
    placeOfRegistration,
    phoneNumber,
    secondNumber,
    email,
    comment
)

fun CargoName.toDb() = DbCargo(id, name, positionId)