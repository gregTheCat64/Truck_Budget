package ru.javacat.ui.login


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.User
import ru.javacat.domain.repo.ApiRepository
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.repo.TrucksRepository

import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val companyRepository: CompaniesRepository,
    private val trucksRepository: TrucksRepository,
    private val trailersRepository: TrailersRepository,
    private val truckDriversRepository: TruckDriversRepository,
    private val apiRepository: ApiRepository
) : ViewModel() {
    private val TAG = "LoginVM"

    private val _user = MutableSharedFlow<User>()
    val user = _user.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading


    fun createDefaultCompany() {
        Log.i(TAG, "createDefaultCompany")

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            companyRepository.createDefaultCompany()
            trucksRepository.createDefaultTruck()
            trailersRepository.createDefaultTrailer()
            truckDriversRepository.createDefaultTruckDriver()
            truckDriversRepository.createMeAsTruckDriver()
            _isLoading.value = false
        }
    }

    fun getUserInfo(token: String) {
        Log.i(TAG, "getUserInfo")
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val result = apiRepository.getUserInfo(token)
                _user.emit(result)
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun downloadBdFromYandexDisk(
        token: String,
        onComplete: (Boolean) -> Unit
    ) {
        Log.i(TAG, "downLoading")
        viewModelScope.launch {
            _isLoading.value = true
            val result = apiRepository.downLoadDatabaseFiles(token)
            onComplete(result)
            _isLoading.value = false
        }
    }


}