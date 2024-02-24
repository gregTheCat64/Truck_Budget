package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TrucksRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewTransportViewModel @Inject constructor(
    private val trucksRepository: TrucksRepository,
    private val trailersRepository: TrailersRepository
): ViewModel() {

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    suspend fun insertNewTruck(truck: Truck) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                trucksRepository.insert(truck)
                _loadState.emit(LoadState.Success)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }

    suspend fun insertNewTrailer(trailer: Trailer) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                trailersRepository.insert(trailer)
                _loadState.emit(LoadState.Success)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}