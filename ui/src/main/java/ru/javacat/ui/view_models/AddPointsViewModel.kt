package ru.javacat.ui.view_models

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.javacat.domain.models.Point
import ru.javacat.domain.repo.OrderRepository
import javax.inject.Inject

@HiltViewModel
class AddPointsViewModel @Inject constructor(
    private val repository: OrderRepository
) {
    private val draftOrder = repository.editedOrder

    suspend fun updatePoints(points: List<Point>) {
        repository.updateOrder(
            draftOrder.value.copy(points = points)
        )
    }
}