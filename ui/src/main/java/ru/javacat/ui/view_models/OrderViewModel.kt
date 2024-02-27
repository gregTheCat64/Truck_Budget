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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.CargoRepository
import ru.javacat.domain.repo.CustomerRepository
import ru.javacat.domain.repo.LocationRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val customerRepository: CustomerRepository,
    private val locationRepository: LocationRepository,
    private val cargoRepostiory: CargoRepository,
    private val orderRepository: OrderRepository
):ViewModel() {

    val editedOrder = orderRepository.editedOrder

    val editedRoute = routeRepository.editedRoute

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

//    private val _points = MutableStateFlow<List<Point>?>(null)
//    val points = _points.asStateFlow()

    val pointList = mutableListOf<Point>()

   var orderList = mutableListOf<Order>()

    private val _customers = MutableStateFlow<List<Customer>?>(null)
    val customers = _customers.asStateFlow()

    private val _locations = MutableStateFlow<List<Location>?>(null)
    val locations = _locations.asStateFlow()

    private val _cargo = MutableStateFlow<List<Cargo>?>(null)
    val cargo = _cargo.asStateFlow()

    private val _pointDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val pointDate: StateFlow<LocalDate> = _pointDate

    fun updateEditedRoute(order: Order){
        orderList.add(order)
        viewModelScope.launch(){
            routeRepository.updateRoute(editedRoute.value.copy(
                orderList = orderList.toList()
            ))

            Log.i("OrderVM", "orderList size: ${editedRoute.value.orderList.size}")
        }
    }

    fun setOrderList(){
        orderList = editedRoute.value.orderList.toMutableList()
    }

    fun setPointDate(localDate: LocalDate){
        viewModelScope.launch {
            _pointDate.emit(localDate)
        }
    }

    fun increaseDay(){
        viewModelScope.launch{
            _pointDate.emit(_pointDate.value.plusDays(1))
        }
    }

    fun decreaseDay(){
        viewModelScope.launch{
            _pointDate.emit(_pointDate.value.minusDays(1))
        }
    }

    fun getCustomers(){
        viewModelScope.launch {
            val result = customerRepository.getAll()
            _customers.emit(result)
        }
    }
    fun searchCustomers(search: String) {
        viewModelScope.launch {
            val result = customerRepository.search(search)
            _customers.emit(result)
        }
    }
    fun setCustomer(t: Customer){
        viewModelScope.launch {
            orderRepository.updateOrder(editedOrder.value.copy(customer = t))
        }
    }

    fun addPoint(point: Point) {
        pointList.add(point)
        viewModelScope.launch {
            Log.i("OrderVM", "points: $pointList")
            orderRepository.updateOrder( editedOrder.value.copy(points = pointList.toList()))
        }
    }

    fun removePoint(point: Point) {
        pointList.remove(point)
        viewModelScope.launch {
            orderRepository.updateOrder(editedOrder.value.copy(points = pointList.toList()))
            //_points.emit(pointList.toList())
        }
    }

    fun insertNewLocation(location: Location){
        viewModelScope.launch(Dispatchers.IO){
            locationRepository.insertLocation(location)
        }
    }

    fun getLocations(){
        viewModelScope.launch(Dispatchers.IO){
            val result = locationRepository.getLocations()
            println("locations: $result")
            _locations.emit(result)
        }
    }

    fun searchLocations(search: String){
        viewModelScope.launch(Dispatchers.IO){
            val result = locationRepository.searchLocations(search)
            println("locations: $result")
            _locations.emit(result)
        }
    }

    fun getCargos(){
        viewModelScope.launch(Dispatchers.IO){
            val result = cargoRepostiory.getAll()
            _cargo.emit(result)
        }
    }

    fun searchCargos(search: String) {
        viewModelScope.launch(Dispatchers.IO){
            val result = cargoRepostiory.search(search)
            _cargo.emit(result)
        }
    }

    fun insertNewCargo(cargo: Cargo){
        viewModelScope.launch(Dispatchers.IO){
            cargoRepostiory.insert(cargo)
        }
    }

    fun saveOrder(order: Order){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                val result = orderRepository.insertOrder(order)
                updateEditedRoute(order)
                orderRepository.clearCurrentOrder()
                //routeRepository.insertRoute(editedRoute.value)
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }


}