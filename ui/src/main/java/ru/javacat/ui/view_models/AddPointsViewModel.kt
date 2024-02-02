package ru.javacat.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Point
import ru.javacat.domain.repo.OrderRepository
import javax.inject.Inject

@HiltViewModel
class AddPointsViewModel @Inject constructor(
    private val repository: OrderRepository
):ViewModel() {
    private val draftOrder = repository.editedOrder

    private var _points= MutableStateFlow<List<Point>?>(null)
    var points = _points.asStateFlow()

    private val pointList = mutableListOf<Point>()

    fun addPoint(point: Point) {
        pointList.add(point)
        viewModelScope.launch {
            _points.emit(pointList.toList())
        }
        Log.i("MYTAG", "points: ${points.value}")
    }

    fun removePoint(point: Point) {
        pointList.remove(point)
        viewModelScope.launch {
            _points.emit(pointList.toList())
        }
    }

    suspend fun updatePoints(points: List<Point>) {
        repository.updateOrder(
            draftOrder.value.copy(points = points)
        )
    }
}