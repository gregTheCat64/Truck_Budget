package ru.javacat.ui.new_route

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Contractor
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.RouteDetails
import ru.javacat.domain.models.SalaryParameters
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.use_case.GetCompaniesUseCase
import ru.javacat.domain.use_case.GetTrailersByCompanyIdUseCase
import ru.javacat.domain.use_case.GetTruckDriversByCompanyIdUseCase
import ru.javacat.domain.use_case.GetTrucksByCompanyIdUseCase
import ru.javacat.ui.LoadState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewRouteViewModel @Inject constructor(
    private val repo: RouteRepository,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val getTrucksUseCase: GetTrucksByCompanyIdUseCase,
    private val getTrailersUseCase: GetTrailersByCompanyIdUseCase,
    private val getTruckDriversUseCase: GetTruckDriversByCompanyIdUseCase,
) : ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    var routeId = MutableStateFlow<Long?>(null)

//    val editedRoute = repo.editedItem

    private var _newRoute = MutableStateFlow<Route?>(Route())
    val newRoute: StateFlow<Route?>
        get() = _newRoute.asStateFlow()

    private val _contractors = MutableStateFlow<List<Company>?>(null)
    val contractors = _contractors.asStateFlow()

    private val _trucks = MutableStateFlow<List<Truck>?>(null)
    val trucks = _trucks.asStateFlow()

    private val _trailers = MutableStateFlow<List<Trailer>?>(null)
    val trailers = _trailers.asStateFlow()

    private val _drivers = MutableStateFlow<List<TruckDriver>?>(null)
    val drivers = _drivers.asStateFlow()

    private var _prepay: Int? = null

    private val date = LocalDate.now()

    //ContractorsPart:
    fun getContractors() {
        viewModelScope.launch(Dispatchers.IO) {
            //_contractors.emit(getCompaniesUseCase.invoke())
            _contractors.value = getCompaniesUseCase.invoke()
        }
    }

    fun setCompany(t: Company) {
        Log.i("NewRouteVM", "setCompany")
        _newRoute.value = newRoute.value?.copy(
            contractor = Contractor(
                company = t,
                driver = null,
                truck = null,
                trailer = null
            )
        )
        Log.i("NewRouteVM", "${_newRoute.value}")
    }

    //TruckPart:
    fun getTrucks(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            //val result = getTrucksUseCase.invoke(id)
            //_trucks.emit(result)
            _trucks.value = getTrucksUseCase.invoke(id)
        }
    }

    fun setTruck(t: Truck) {
        _newRoute.value = _newRoute.value?.copy(
            contractor = _newRoute.value?.contractor?.copy(
                truck = t
            )
        )
    }

    //TrailerPart:
    fun getTrailers(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTrailersUseCase.invoke(id)
            _trailers.value = result
        }
    }

    fun setTrailer(t: Trailer) {
        _newRoute.value = _newRoute.value?.copy(
            contractor = _newRoute.value?.contractor?.copy(
                trailer = t
            )
        )
    }

    //TruckDriverPart:
    fun getDriver(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTruckDriversUseCase.invoke(id)
            _drivers.value = result
        }
    }

    fun setDriver(t: TruckDriver) {
        _newRoute.value = _newRoute.value?.copy(
            contractor = _newRoute.value?.contractor?.copy(
                driver = t
            )
        )
    }

    //NewRoutePart:
    fun saveNewRoute(route: Route) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                    val result = repo.insert(
                    route.copy(startDate = date, prepayment = _prepay?:0)
                )
                routeId.value = result
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
                _newRoute.value = newRoute.value?.copy(
                    id = 0,
                    contractor = lastRoute?.contractor,
                    startDate = null,
                    endDate = null,
                    orderList = emptyList(),
                    routeDetails = RouteDetails(),
                    salaryParameters = SalaryParameters(),
                    prepayment = 0,
                    driverSalary = 0f,
                    routeContractorsSum = 0,
                    totalExpenses = 0f,
                    moneyToPay = 0f,
                    revenue = 0,
                    profit = 0f,
                    isFinished = false
                )
            }
    }

    fun setPrepayment(prepay: Int) {
        _prepay = prepay
    }

    fun clearRouteId(){
        routeId.value = null
    }

}