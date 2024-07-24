package ru.javacat.domain.models

data class StatsModel (
    val companyRoutesCount: Int? = null,
    val notCompanyRoutesCount: Int? = null,
    val companyOrdersCount: Int? = null,
    val notCompanyOrdersCount: Int? = null,
    val totalProfit: Long? = null,
    val companyAverageMonthlyProfit: Long? = null,
    val notCompanyAverageMonthlyProfit: Long? = null,
    val monthlyProfitList: List<MonthlyProfit>? = null
)