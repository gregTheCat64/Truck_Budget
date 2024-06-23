package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.TrailersRepository
import javax.inject.Inject

@HiltViewModel
class TrailersViewModel @Inject constructor(
    private val repository: TrailersRepository,
    companiesRepository: CompaniesRepository
): ViewModel() {
    private val _trailers = MutableStateFlow<List<Trailer>?>(null)
    val trailers = _trailers.asStateFlow()

    val currentCompany = companiesRepository.chosenItem

}