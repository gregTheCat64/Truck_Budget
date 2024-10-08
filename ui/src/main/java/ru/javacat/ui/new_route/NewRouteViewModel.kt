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
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.use_case.GetCompaniesUseCase
import ru.javacat.domain.use_case.GetTrailersByCompanyIdUseCase
import ru.javacat.domain.use_case.GetTruckDriversByCompanyIdUseCase
import ru.javacat.domain.use_case.GetTrucksByCompanyIdUseCase
import ru.javacat.domain.use_case.SaveNewCompanyUseCase
import ru.javacat.domain.use_case.SaveNewTrailerUseCase
import ru.javacat.domain.use_case.SaveNewTruckDriverUseCase
import ru.javacat.domain.use_case.SaveNewTruckUseCase
import ru.javacat.ui.LoadState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewRouteViewModel @Inject constructor(
    private val repo: RouteRepository,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    private val saveNewCompanyUseCase: SaveNewCompanyUseCase,
    private val getTrucksUseCase: GetTrucksByCompanyIdUseCase,
    private val saveNewTruckUseCase: SaveNewTruckUseCase,
    private val getTrailersUseCase: GetTrailersByCompanyIdUseCase,
    private val saveNewTrailerUseCase: SaveNewTrailerUseCase,
    private val getTruckDriversUseCase: GetTruckDriversByCompanyIdUseCase,
    private val saveNewTruckDriverUseCase: SaveNewTruckDriverUseCase
) : ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    var routeId = MutableStateFlow<Long?>(null)

//    val editedRoute = repo.editedItem

    private var _currentRoute = MutableStateFlow<Route?>(Route())
    val currentRoute: StateFlow<Route?>
        get() = _currentRoute.asStateFlow()

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
        _currentRoute.value = currentRoute.value?.copy(
            contractor = Contractor(
                company = t,
                driver = null,
                truck = null,
                trailer = null
            )
        )
        Log.i("NewRouteVM", "${_currentRoute.value}")
    }

    fun saveNewContractor(company: Company){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val newCompanyId = saveNewCompanyUseCase.invoke(company)
                val newCompany = company.copy(id = newCompanyId)
                setCompany(newCompany)
                _loadState.emit(LoadState.Success.GoBack)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
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
        _currentRoute.value = _currentRoute.value?.copy(
            contractor = _currentRoute.value?.contractor?.copy(
                truck = t
            )
        )
    }

    fun saveNewTruck(t: Truck){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val newTruckId = saveNewTruckUseCase.invoke(t)
                val newTruck = t.copy(id = newTruckId)
                setTruck(newTruck)
                _loadState.emit(LoadState.Success.GoBack)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    //TrailerPart:
    fun getTrailers(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTrailersUseCase.invoke(id)
            _trailers.value = result
        }
    }

    fun setTrailer(t: Trailer) {
        _currentRoute.value = _currentRoute.value?.copy(
            contractor = _currentRoute.value?.contractor?.copy(
                trailer = t
            )
        )
    }

    fun saveNewTrailer(t: Trailer){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val newTrailerId = saveNewTrailerUseCase.invoke(t)
                val newTrailer = t.copy(id = newTrailerId)
                setTrailer(newTrailer)
                _loadState.emit(LoadState.Success.GoBack)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    //TruckDriverPart:
    fun getDriver(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTruckDriversUseCase.invoke(id)
            _drivers.value = result
        }
    }

    fun setDriver(t: TruckDriver) {
        _currentRoute.value = _currentRoute.value?.copy(
            contractor = _currentRoute.value?.contractor?.copy(
                driver = t
            )
        )
    }

    fun saveNewTruckDriver(td: TruckDriver){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val newTruckDriverId = saveNewTruckDriverUseCase.invoke(td)
                val newTruckDriver = td.copy(id = newTruckDriverId)
                setDriver(newTruckDriver)
                _loadState.emit(LoadState.Success.GoBack)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
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

    fun restoreCurrentRoute(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _currentRoute.value = repo.getById(id)
        }
    }

    fun setLastRouteToEditedRoute() {
        viewModelScope.launch(Dispatchers.IO) {
            val lastRoute = repo.lastRoute
            Log.i("NewRouteVM", "lastRoute: $lastRoute")
                _currentRoute.value = currentRoute.value?.copy(
                    id = 0,
                    contractor = lastRoute?.contractor,
                    startDate = null,
                    endDate = null,
                    orderList = emptyList(),
                    routeDetails = null,
                    salaryParameters = null,
                    prepayment = 0,
                    driverSalary = null,
                    routeContractorsSum = null,
                    totalExpenses = null,
                    moneyToPay = null,
                    revenue = null,
                    profit = null,
                    isFinished = false
                )
            Log.i("NewRouteVM", "_new_route: ${_currentRoute.value}")
            }
    }

    fun setPrepayment(prepay: Int) {
        _prepay = prepay
    }

    fun clearRouteId(){
        routeId.value = null
    }

}