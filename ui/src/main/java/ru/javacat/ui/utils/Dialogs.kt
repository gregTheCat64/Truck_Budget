package ru.javacat.ui.utils

import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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