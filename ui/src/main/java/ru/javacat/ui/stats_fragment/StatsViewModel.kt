package ru.javacat.ui.stats_fragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.MonthlyProfit
import ru.javacat.domain.models.StatsModel
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.ExpenseRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    val repository: CompaniesRepository,
    val routeRepository: RouteRepository,
    val orderRepository: OrderRepository,
    val expenseRepository: ExpenseRepository
): ViewModel() {

    val monthlyProfitList = MutableStateFlow<List<MonthlyProfit>?>(null)

    private val _stats = MutableStateFlow(StatsModel())
    val stats: StateFlow<StatsModel> get() = _stats

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()


    fun updateStats(year: String){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)

            try {
                val companyRoutesCountResult = routeRepository.getCompanyRoutesCountByYear(year)
                Log.i("statsVM", "CompanyCount: $companyRoutesCountResult")

                val companyOrdersCountResult = orderRepository.getCompanyOrdersCountByYear(year)
                Log.i("statsVM", "CompanyCount: $companyOrdersCountResult")

                val notCompanyOrdersCountResult = orderRepository.getNotCompanyOrdersCountByYear(year)
                Log.i("statsVM", "notCompanyCount: $notCompanyOrdersCountResult")

                val monthlyProfitResult = routeRepository.getMonthlyIncomeByYear(year)
                val notCompanyProfitResult = routeRepository.getMonthlyIncomeByYearNotCompanyTransport(year)
                Log.i("statsVM", "notCompanyProfitResult: $notCompanyProfitResult")

                val averageMonthlyProfit = monthlyProfitResult.map { it.totalProfit }.average().toInt().toLong()
                var totalYearProfit = 0L
                monthlyProfitResult.forEach {
                    totalYearProfit += it.totalProfit
                }

                val notCompanyAverageMonthlyProfit = notCompanyProfitResult.map { it.totalProfit }.average().toInt().toLong()
                var notCompanyTotalYearProfit = 0L
                notCompanyProfitResult.forEach {
                    notCompanyTotalYearProfit += it.totalProfit
                }

                val monthlyExpenseResult = expenseRepository.getMonthlyExpenseByYear(year)

                _stats.value = _stats.value.copy(
                    companyRoutesCount = companyRoutesCountResult,
                    companyOrdersCount = companyOrdersCountResult,
                    notCompanyOrdersCount = notCompanyOrdersCountResult,
                    totalProfit = totalYearProfit,
                    notCompanyTotalProfit = notCompanyTotalYearProfit,
                    companyAverageMonthlyProfit = averageMonthlyProfit,
                    notCompanyAverageMonthlyProfit = notCompanyAverageMonthlyProfit,
                    monthlyProfitList = monthlyProfitResult,
                    monthlyExpenseList = monthlyExpenseResult
                )

                //monthlyProfitList.emit(monthlyProfitResult)

                _loadState.emit(LoadState.Success.OK)

            }catch (e: Exception){
                println(e.message)
            }
        }

    }
}