package ru.javacat.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
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
): ViewModel() {
    private val TAG = "LoginVM"

    private val _user = MutableSharedFlow<User>()
    val user = _user.asSharedFlow()

    fun createDefaultCompany(){
        Log.i(TAG, "createDefaultCompany")
        viewModelScope.launch(Dispatchers.IO) {
            companyRepository.createDefaultCompany()
            trucksRepository.createDefaultTruck()
            trailersRepository.createDefaultTrailer()
            truckDriversRepository.createDefaultTruckDriver()
            truckDriversRepository.createMeAsTruckDriver()
        }
    }

    fun getUserInfo(token: String){
        Log.i(TAG, "getUserInfo")
        viewModelScope.launch(Dispatchers.IO){
            try {
                val result = apiRepository.getUserInfo(token)
                _user.emit(result)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}