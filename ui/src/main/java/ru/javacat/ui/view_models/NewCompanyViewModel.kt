package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.use_case.ClearTrailerUseCase
import ru.javacat.domain.use_case.ClearTruckDriverUseCase
import ru.javacat.domain.use_case.ClearTruckUseCase
import ru.javacat.domain.use_case.SetCompanyUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewCompanyViewModel @Inject constructor(
    private val repository: CompaniesRepository,
    private val setCompanyUseCase: SetCompanyUseCase,
    private val clearTruckDriverUseCase: ClearTruckDriverUseCase,
    private val clearTruckUseCase: ClearTruckUseCase,
    private val clearTrailerUseCase: ClearTrailerUseCase
): ViewModel() {

    var editedCustomer = MutableStateFlow<Company?>(null)

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()
    suspend fun getCustomerById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedCustomer.emit(repository.getById(id))
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun saveNewCustomer(company: Company, isNeedToSet: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                repository.insert(company)
                if (isNeedToSet){
                    setCompanyUseCase.invoke(company)
                    clearTruckUseCase.invoke()
                    clearTrailerUseCase.invoke()
                    clearTruckDriverUseCase.invoke()
                }
                _loadState.emit(LoadState.Success.GoBack)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }

}