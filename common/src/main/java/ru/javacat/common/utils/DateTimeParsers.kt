package ru.javacat.common.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDate.asDayOfWeek(): String = format(DateTimeFormatter.ofPattern("EEE"))


fun LocalDate.asDayAndMonthFully(): String = format(DateTimeFormatter.ofPattern("d MMMM"))

fun LocalDate.asDayAndMonthShortly(): String = format(DateTimeFormatter.ofPattern("d MMM"))

fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))


fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm"))

//fun String.toLocalTime(): LocalTime =
//    LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm"))


fun LocalDateTime.asHour(): String = format(DateTimeFormatter.ofPattern("h a"))


fun LocalDateTime.asTime(): String = format(DateTimeFormatter.ofPattern("hh:mm"))