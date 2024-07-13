package ru.javacat.ui.new_route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.use_case.ClearTrailerUseCase
import ru.javacat.domain.use_case.GetTrailersByCompanyIdUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class ChooseTrailerViewModel @Inject constructor(
    private val repository: RouteRepository,
    private val getTrailersUseCase: GetTrailersByCompanyIdUseCase,
): ViewModel() {
    val editedRoute = repository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _trailers = MutableStateFlow<List<Trailer>?>(null)
    val trailers = _trailers.asStateFlow()

    fun getTrailers(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTrailersUseCase.invoke(id)
            _trailers.emit(result)
        }
    }

    fun setTrailer(t: Trailer) {
        viewModelScope.launch {
            editedRoute.value?.let {
                repository.updateEditedItem(
                    it.copy(
                        contractor = it.contractor?.copy(
                            trailer = t
                        )
                    )
                )
            }
        }
    }

    //    fun searchTrailers(s: String){
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = trailersRepository.search(s)
//            _trailers.emit(result)
//        }
//    }
}