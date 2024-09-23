package ru.javacat.ui.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import ru.javacat.domain.models.YearHolder
import ru.javacat.ui.OneInputValueDialogFragment
import ru.javacat.ui.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

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

fun Fragment.showYearCalendar(onYearSelected: (Int) -> Unit) {
    val builder = AlertDialog.Builder(requireContext())
    val inflater: LayoutInflater = layoutInflater
    val dialogView = inflater.inflate(R.layout.dialog_year_picker, null)
    builder.setView(dialogView)

    val numberPicker: NumberPicker = dialogView.findViewById(R.id.numberPickerYear)

    numberPicker.minValue = 2000
    numberPicker.maxValue = 2100

    numberPicker.value = YearHolder.selectedYear

    // Добавьте кнопки "ОК" и "Отмена"
    builder.setTitle("Выберите год")
        .setPositiveButton("OK") { _, _ ->
            val selectedYear = numberPicker.value
            onYearSelected(selectedYear)
        }
        .setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

    // Покажите диалог
    builder.create().show()

}

fun FragmentManager.showOneInputDialog(oldValue: String?, typeOfValue: String){
    val dialogFragment = OneInputValueDialogFragment()
    val dialogBundle = Bundle()
    if (oldValue != null) {
        dialogBundle.putString(FragConstants.OLD_VALUE, oldValue)
    }
    dialogBundle.putString(FragConstants.TYPE_OF_VALUE, typeOfValue)
    dialogFragment.arguments = dialogBundle
    dialogFragment.show(this, "")
}