package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Staff
import ru.javacat.domain.repo.StaffRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class NewDriverViewModel @Inject constructor(
    private val repository: StaffRepository
):ViewModel() {

     fun addDriver(driver: Staff){

        //val id = passportSerial.toString() + passportNumber.toString()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertDriver(driver)
            }catch (e: Error){
                e.printStackTrace()
            }

        }

    }
}