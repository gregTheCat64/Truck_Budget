package ru.javacat.data.db.mappers

import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.data.db.entities.DbLocation
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbPoint
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbStaff
import ru.javacat.data.db.entities.DbVehicle
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.Staff
import ru.javacat.domain.models.Vehicle

fun Route.toDb() = DbRoute(
    id,
    startDate.toString(),
    endDate.toString(),
    driver,
    truck,
    trailer,
    prepayment,
    fuelUsedUp,
    fuelPrice,
    routeExpenses,
    routeDuration,
    driverSalary,
    moneyToPay,
    income,
    netIncome,
    isFinished
)

fun Order.toDb(route: Route) = DbOrder(
    id,
    routeId,
    route.driver,
    route.truck,
    route.trailer,
    price,
    customer.atiNumber,
    paymentDeadline.toString(),
    sentDocsNumber,
    docsReceived.toString(),
    status
)

fun Point.toDb(order: Order) = DbPoint(
    id, order.id, location, arrivalDate.toString()
)

fun Vehicle.toDb() = DbVehicle(
    regNumber, vin, model, type, yearOfManufacturing
)

fun Staff.toDb() = DbStaff(
    fullName,
    duty,
    passportNumber,
    passportReceivedDate,
    passportReceivedPlace,
    driveLicenseNumber,
    placeOfRegistration
)

fun Location.toDb() = DbLocation(
    name
)

fun Customer.toDb() = DbCustomer(
    companyName, atiNumber, companyPhone
)

fun Employee.toDb() = DbEmployee(
    id, customerAtiNumber, name, phoneNumber, secondNumber, email
)