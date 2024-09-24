package ru.javacat.ui.trailer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.use_case.GetTrailerUseCase
import ru.javacat.domain.use_case.HideTrailerUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class TrailerInfoViewModel @Inject constructor(
    private val getTrailerUseCase: GetTrailerUseCase,
    private val hideTrailerUseCase: HideTrailerUseCase
): ViewModel() {
    val trailer = MutableStateFlow<Trailer?>(null)

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    fun getTrailer(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                trailer.value = getTrailerUseCase.invoke(id)
                _loadState.emit(LoadState.Success.OK)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun hideTrailer(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                hideTrailerUseCase.invoke(id)
                _loadState.emit(LoadState.Success.Removed)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}