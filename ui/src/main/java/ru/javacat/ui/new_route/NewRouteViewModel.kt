package ru.javacat.ui.new_route

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.SalaryParameters
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.repo.TrucksRepository
import ru.javacat.domain.use_case.SetCompanyUseCase
import ru.javacat.domain.use_case.SetTrailerUseCase
import ru.javacat.domain.use_case.SetTruckDriverUseCase
import ru.javacat.domain.use_case.SetTruckUseCase
import ru.javacat.ui.LoadState
import ru.javacat.ui.utils.FragConstants
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewRouteViewModel @Inject constructor(
    private val repo: RouteRepository,
    trucksRepository: TrucksRepository,
    trailersRepository: TrailersRepository,
    truckDriversRepository: TruckDriversRepository,
    companiesRepository: CompaniesRepository,
    private val setCompanyUseCase: SetCompanyUseCase,
    private val setTruckDriverUseCase: SetTruckDriverUseCase,
    private val setTruckUseCase: SetTruckUseCase,
    private val setTrailerUseCase: SetTrailerUseCase,
) : ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    var routeId = MutableStateFlow<Long?>(null)

    val editedRoute = repo.editedItem

    private var _prepay: Int? = null

    private val date = LocalDate.now()


    fun saveNewRoute(route: Route) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
//                val _countRoute: SalaryCount =
//                    if (route.contractor?.company?.id == FragConstants.MY_COMPANY_ID) {
//                        SalaryCount(prepayment = _prepay)
//                    } else SalaryCount(0,null)
                val result = repo.insert(
                    route.copy(startDate = date, prepayment = _prepay?:0)
                )
                routeId.emit(result)

                _loadState.emit(LoadState.Success.OK)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun setLastRouteToEditedRoute() {
        viewModelScope.launch(Dispatchers.IO) {
            val lastRoute = repo.lastRoute
            Log.i("NewRouteVM", "lastRoute: $lastRoute")
            lastRoute?.contractor?.apply {
//                this.company?.let { setCompanyUseCase.invoke(it) }
//                this.driver?.let { setTruckDriverUseCase.invoke(it) }
//                this.truck?.let { setTruckUseCase.invoke(it) }
//                this.trailer?.let { setTrailerUseCase.invoke(it) }
                editedRoute.value?.copy(
                    id = 0,
                    contractor = lastRoute.contractor,
                    startDate = null,
                    endDate = null,
                    orderList = emptyList(),
                    salaryParameters = SalaryParameters(),
                    prepayment = 0,
                    fuelPrice = 0f,
                    fuelUsedUp = 0,
                    extraExpenses = 0,
                    roadFee = 0,
                    extraPoints = 0,
                    routeDuration = 0,
                    routeDistance = 0,
                    driverSalary = 0f,
                    contractorsCost = 0,
                    totalExpenses = 0f,
                    moneyToPay = 0f,
                    revenue = 0,
                    profit = null,
                    isFinished = false
                )?.let {
                    repo.updateEditedItem(
                        it
                    )
                }
            }
        }
    }

    fun setRouteParameters(prepay: Int) {
        _prepay = prepay
    }

}