package ru.javacat.ui.utils

import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.ui.OneInputValueDialogFragment
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun FragmentManager.showCalendar(callback: (LocalDate) -> Unit) {
    val picker = MaterialDatePicker.Builder.datePicker()
        .build()

    picker.addOnPositiveButtonClickListener {
        val date = LocalDateTime
            .ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            .toLocalDate()
        callback(date)
    }
    picker.show(this, "materialDatePicker")
}

fun FragmentManager.showOneInputDialog(oldValue: String, typeOfValue: String){
    val dialogFragment = OneInputValueDialogFragment()
    val dialogBundle = Bundle()
    dialogBundle.putString(FragConstants.OLD_VALUE, oldValue)
    dialogBundle.putString(FragConstants.TYPE_OF_VALUE, typeOfValue)
    dialogFragment.arguments = dialogBundle
    dialogFragment.show(this, "")
}