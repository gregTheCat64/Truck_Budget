package ru.javacat.ui.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

fun Fragment.showDeleteConfirmationDialog(removingSubject: String, onConfirm: () -> Unit) {
    val context: Context = this.requireContext()

    AlertDialog.Builder(context)
        .setTitle("Подтверждение удаления")
        .setMessage("Вы точно хотите удалить $removingSubject?")
        .setPositiveButton("Да") { dialog, _ ->
            onConfirm()
            dialog.dismiss()
        }
        .setNegativeButton("Нет") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}