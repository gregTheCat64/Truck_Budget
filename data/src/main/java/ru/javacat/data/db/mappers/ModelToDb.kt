package ru.javacat.data.db.mappers

import ru.javacat.data.db.entities.DbCargo
import ru.javacat.data.db.entities.DbCompany
import ru.javacat.data.db.entities.DbExpense
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
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Expense
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
    contractor?.company?.id,
    contractor?.driver?.id,
    contractor?.truck?.id,
    contractor?.trailer?.id,
    routeDetails,
    salaryParameters,
    prepayment,
    driverSalary,
    routeContractorsSum,
    totalExpenses,
    moneyToPay,
    isPaidToContractor,
    revenue,
    profit,
    isFinished
)

fun Order.toDb() = DbOrder(
    id,
    routeId?:0L,
    points.map { it.toDb() },
    points[0].arrivalDate.toString(),
    price?:0,
    payType,
    contractorPrice,
    payTypeToContractor,
    commission,
    customer?.id?:0,
    manager?.id,
    contractor?.company?.id,
    contractor?.driver?.id?: 0,
    contractor?.truck?.id?: 0,
    contractor?.trailer?.id,
    cargo?: Cargo(20, 82, "ТНП", true, false,false),
    extraConditions,
    daysToPay,
    daysToPayToContractor,
    paymentDeadline?.toString(),
    sentDocsNumber,
    docsReceived?.toString(),
    isPaidByCustomer,
    isPaidToContractor
)

fun Point.toDb() = DbPoint(
    id,  location, arrivalDate.toString()
)

fun Truck.toDb() = DbTruck(
    id,isHidden, companyId, regNumber, regionCode, vin, model, type, yearOfManufacturing
)

fun Trailer.toDb() = DbTrailer(
    id,isHidden, companyId, regNumber, regionCode, vin, model, type, yearOfManufacturing
)

fun TruckDriver.toDb() = DbTruckDriver(
    id,
    positionId,
    isHidden,
    customerId,
    firstName,
    middleName,
    surname,
    passportNumber,
    passportReceivedDate?.toString(),
    passportReceivedPlace,
    driveLicenseNumber,
    placeOfRegistration,
    phoneNumber,
    secondNumber,
    comment,
    salaryParameters
)

fun Location.toDb() = DbLocation(
    id, nameToShow, isHidden, positionId
)

fun Company.toDb() = DbCompany(
    id, nameToShow, isHidden, atiNumber, companyPhone, formalAddress, postAddress, shortName,positionId,
)

fun Manager.toDb() = DbManager(
    id,
    positionId,
    isHidden,
    customerId,
    firstName,
    middleName,
    surname,
    passportNumber,
    passportReceivedDate?.toString(),
    passportReceivedPlace,
    placeOfRegistration,
    phoneNumber,
    secondNumber,
    email,
    comment
)

fun CargoName.toDb() = DbCargo(id, nameToShow, isHidden, positionId)

fun Expense.toDb() = DbExpense(id, nameToShow, isHidden, positionId, description, date.toString(), price)