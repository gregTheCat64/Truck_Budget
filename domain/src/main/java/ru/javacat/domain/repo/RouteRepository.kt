package ru.javacat.domain.repo

import ru.javacat.domain.models.MonthlyProfit
import ru.javacat.domain.models.Route

interface RouteRepository: BaseRepository<Route, Long> {

    val lastRoute: Route?
    suspend fun updateRouteToDb(route: Route)

    suspend fun getMonthlyIncomeByYear(year: String): List<MonthlyProfit>

    suspend fun getCompanyRoutesCountByYear(year: String): Int

    suspend fun getNotCompanyRoutesCountByYear(year: String): Int


}