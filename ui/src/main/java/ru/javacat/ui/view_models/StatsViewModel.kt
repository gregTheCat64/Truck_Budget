package ru.javacat.ui.view_models

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
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    val repository: CompaniesRepository,
    val routeRepository: RouteRepository,
    val orderRepository: OrderRepository
): ViewModel() {

    val monthlyProfitList = MutableStateFlow<List<MonthlyProfit>?>(null)

    private val _stats = MutableStateFlow(StatsModel())
    val stats: StateFlow<StatsModel> get() = _stats

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()


    fun getMonthlyIncomeByYear(year: String){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            val result = routeRepository.getMonthlyIncomeByYear(year)
            val averageMonthlyProfit = result.map { it.totalProfit }.average().toInt().toLong()
            var totalYearProfit = 0L
            result.forEach {
                totalYearProfit += it.totalProfit
            }
            _stats.value = _stats.value.copy(
                totalProfit = totalYearProfit,
                companyAverageMonthlyProfit = averageMonthlyProfit
                )
            monthlyProfitList.emit(result)
            _loadState.emit(LoadState.Success.OK)
        }
    }

    fun getCompanyRoutesCountByYear(year: String){
        viewModelScope.launch(Dispatchers.IO){
            val result = routeRepository.getCompanyRoutesCountByYear(year)
            Log.i("statsVM", "CompanyCount: $result")
            _stats.value = _stats.value.copy(companyRoutesCount = result)
        }
    }

    fun getNotCompanyRoutesCountByYear(year: String){
        viewModelScope.launch(Dispatchers.IO){
            val result = routeRepository.getNotCompanyRoutesCountByYear(year)
            Log.i("statsVM", "notCompanyCount: $result")
            _stats.value = _stats.value.copy(notCompanyOrdersCount = result)
        }
    }

    fun getCompanyOrdersCountByYear(year: String){
        viewModelScope.launch(Dispatchers.IO){
            val result = orderRepository.getCompanyOrdersCountByYear(year)
            Log.i("statsVM", "CompanyCount: $result")
            _stats.value = _stats.value.copy(companyOrdersCount = result)
        }
    }

    fun getNotCompanyOrdersCountByYear(year: String){
        viewModelScope.launch(Dispatchers.IO){
            val result = orderRepository.getNotCompanyOrdersCountByYear(year)
            Log.i("statsVM", "notCompanyCount: $result")
            _stats.value = _stats.value.copy(notCompanyOrdersCount = result)
        }
    }
}