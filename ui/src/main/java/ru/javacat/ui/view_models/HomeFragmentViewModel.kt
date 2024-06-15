package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    val repository: CompaniesRepository
): ViewModel() {

    val homeCustomer = MutableStateFlow<Company?>(null)

    fun getHomeCustomer(){
        viewModelScope.launch(Dispatchers.IO){
            //зарезервированный id для пользователя
            val result = repository.getById(-1)
            homeCustomer.emit(result)
        }
    }
}