package ru.javacat.ui.edit_order

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
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Point
import ru.javacat.domain.repo.LocationRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditPointsViewModel @Inject constructor(
    routeRepository: RouteRepository,
    private val locationRepository: LocationRepository,
    private val orderRepository: OrderRepository
): ViewModel() {
    val editedOrder = orderRepository.editedItem
    val editedRoute = routeRepository.editedItem

    private var _points = MutableStateFlow<List<Point>?>(null)
    var points = _points.asStateFlow()

    private val _locations = MutableStateFlow<List<Location>?>(null)
    val locations = _locations.asStateFlow()

    private var pointList = mutableListOf<Point>()

    private var _pointDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val pointDate: StateFlow<LocalDate> = _pointDate

    val orderList = editedRoute.value?.orderList?.toMutableList()

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()



    fun getLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = locationRepository.getLocations()
            println("locations: $result")
            _locations.emit(result)
        }
    }

    fun searchLocations(search: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = locationRepository.searchLocations(search)
            println("locations: $result")
            _locations.emit(result)
        }
    }

    fun setPointDate(localDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _pointDate.emit(localDate)
        }
    }

    fun increaseDay() {
        viewModelScope.launch(Dispatchers.IO) {
            _pointDate.emit(_pointDate.value.plusDays(1))
        }
    }

    fun decreaseDay() {
        viewModelScope.launch(Dispatchers.IO) {
            _pointDate.emit(_pointDate.value.minusDays(1))
        }
    }

    fun addPoint(locationName: String, position: Int) {
        pointList = editedOrder.value?.points?.toMutableList()!!
        val refinedPosition = if (position == 0) {
            (pointList.lastOrNull()?.position ?: 0) + 1
        } else {
            //pointList.removeAt(position-1)
            position
        }
        println("refinedPosition = $refinedPosition")
        val point = Point(refinedPosition, locationName, pointDate.value)

        if (position == 0) {
            pointList.add(point)
        } else {
            pointList[refinedPosition-1] = point
        }

        //добавить редактирование
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("OrderVM", "points: $pointList")
            _loadState.emit(LoadState.Loading)
            try {
                editedOrder.value?.copy(points = pointList.toList())
                    ?.let { orderRepository.updateEditedItem(it) }
                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }

    fun insertNewLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.insertLocation(location)
        }
    }

    fun removeLocation(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                locationRepository.removeLocation(id)
                val result = locationRepository.getLocations()
                println("locations: $result")
                _locations.emit(result)
                _loadState.emit(LoadState.Success.Removed)
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }
}