package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import ru.javacat.domain.models.Staff
import ru.javacat.domain.repo.StaffRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewDriverViewModel @Inject constructor(
    private val repository: StaffRepository
):ViewModel() {

    suspend fun addDriver(
        id: String,
        fullName: String,
        passportSerial: String,
        passportNumber: String,
        passportReceivedDate: String,
        passportReceivedPlace: String,
        driveLicenseNumber: String,
        placeOfRegistration: String
        ){

        //val id = passportSerial.toString() + passportNumber.toString()

        val newDriver = Staff(
            id, fullName, passportSerial, passportNumber, passportReceivedDate, passportReceivedPlace, driveLicenseNumber, placeOfRegistration
        )
        repository.insertDriver(newDriver)
    }
}