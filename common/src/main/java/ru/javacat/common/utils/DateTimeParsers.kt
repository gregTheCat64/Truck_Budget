package ru.javacat.common.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

fun LocalDate.toLong(): Long {
    return this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun Long.toLocalDate(): LocalDate =
    Date(this).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

fun LocalDate.asDayOfWeek(): String = format(DateTimeFormatter.ofPattern("EEE"))


fun LocalDate.asDayAndMonthFully(): String = format(DateTimeFormatter.ofPattern("d MMMM"))

fun LocalDate.asDayAndMonthShortly(): String = format(DateTimeFormatter.ofPattern("d MMM"))

fun String?.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))


fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm"))

//fun String.toLocalTime(): LocalTime =
//    LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm"))


fun LocalDateTime.asHour(): String = format(DateTimeFormatter.ofPattern("h a"))


fun LocalDateTime.asTime(): String = format(DateTimeFormatter.ofPattern("hh:mm"))


fun String.toMonth(): Month? {
    return when (this.toIntOrNull()) {
        1 -> Month.JANUARY
        2 -> Month.FEBRUARY
        3 -> Month.MARCH
        4 -> Month.APRIL
        5 -> Month.MAY
        6 -> Month.JUNE
        7 -> Month.JULY
        8 -> Month.AUGUST
        9 -> Month.SEPTEMBER
        10 -> Month.OCTOBER
        11 -> Month.NOVEMBER
        12 -> Month.DECEMBER
        else -> null // Если передан неверный номер месяца
    }
}

fun Month.asShortMonth(): String = this.getDisplayName(TextStyle.SHORT, Locale.getDefault())

//enum class Month(val number: Int) {
//    JANUARY(1),
//    FEBRUARY(2),
//    MARCH(3),
//    APRIL(4),
//    MAY(5),
//    JUNE(6),
//    JULY(7),
//    AUGUST(8),
//    SEPTEMBER(9),
//    OCTOBER(10),
//    NOVEMBER(11),
//    DECEMBER(12)
//}