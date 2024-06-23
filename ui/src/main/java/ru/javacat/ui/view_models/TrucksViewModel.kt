package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject

@HiltViewModel
class TrucksViewModel @Inject constructor(
    private val repository: TrucksRepository,
    companiesRepository: CompaniesRepository
): ViewModel() {
    private val _trucks = MutableStateFlow<List<Truck>?>(null)
    val trucks = _trucks.asStateFlow()

}