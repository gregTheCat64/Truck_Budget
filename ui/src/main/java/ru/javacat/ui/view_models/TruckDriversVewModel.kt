package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject

@HiltViewModel
class TruckDriversVewModel @Inject constructor(
    private val repository: TruckDriversRepository
): ViewModel() {

    private val _drivers = MutableStateFlow<List<TruckDriver>?>(null)
    val drivers = _drivers.asStateFlow()

    fun getDriverList(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getAll()
            _drivers.emit(result)
        }
    }
}