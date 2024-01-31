package ru.javacat.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.javacat.domain.models.Point
import ru.javacat.domain.repo.OrderRepository
import javax.inject.Inject

@HiltViewModel
class AddPointsViewModel @Inject constructor(
    private val repository: OrderRepository
):ViewModel() {
    private val draftOrder = repository.editedOrder

    private var _points: MutableLiveData<ArrayList<Point>> = MutableLiveData()
    var points: LiveData<ArrayList<Point>> = _points

    fun addPoint(point: Point) {
        _points.value?.add(point)
    }
    suspend fun updatePoints(points: List<Point>) {
        repository.updateOrder(
            draftOrder.value.copy(points = points)
        )
    }
}