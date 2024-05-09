package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.javacat.domain.repo.RouteRepository
import javax.inject.Inject

@HiltViewModel
class RouteCountViewModel @Inject constructor(
    private val routeRepository: RouteRepository
): ViewModel() {
    val editedRoute = routeRepository.editedItem
}