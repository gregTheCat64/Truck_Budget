package ru.javacat.ui

import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun showCalendar(fm: FragmentManager, editText: EditText){
    val picker = MaterialDatePicker.Builder.datePicker()
        .build()
    picker.addOnPositiveButtonClickListener {
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
        val timeInMillis = dateFormatter.format(
            Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault()).toLocalDate()
        )
        editText.setText(timeInMillis)
    }
    picker.show(fm, "materialDatePicker")
}