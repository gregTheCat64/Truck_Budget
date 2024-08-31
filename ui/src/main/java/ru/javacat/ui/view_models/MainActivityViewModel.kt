package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val companyRepository: CompaniesRepository,
    private val trucksRepository: TrucksRepository,
    private val trailersRepository: TrailersRepository,
    private val truckDriversRepository: TruckDriversRepository
): ViewModel() {

    fun createDefaultCompany(){
        viewModelScope.launch(Dispatchers.IO) {
            companyRepository.createDefaultCompany()
            trucksRepository.createDefaultTruck()
            trailersRepository.createDefaultTrailer()
            truckDriversRepository.createDefaultTruckDriver()
        }
    }
}