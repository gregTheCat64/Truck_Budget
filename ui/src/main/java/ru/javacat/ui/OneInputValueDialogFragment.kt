package ru.javacat.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import ru.javacat.ui.databinding.FragmentOneInputValueDialogBinding
import ru.javacat.ui.utils.FragConstants


class OneInputValueDialogFragment : DialogFragment(R.layout.fragment_one_input_value_dialog) {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentOneInputValueDialogBinding.inflate(layoutInflater)

        val oldValue = arguments?.getString(FragConstants.OLD_VALUE)
        val typeOfValue = arguments?.getString(FragConstants.TYPE_OF_VALUE)

        //typeOfValue.let { Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show() }
        val hint = when (typeOfValue) {
            FragConstants.DAYS_TO_PAY -> "Срок оплаты, дней"
            FragConstants.PRICE -> "Цена, руб."
            FragConstants.DOCS_NUMBER -> "Номер отправления"
            else -> "Новое Значение"
        }
        binding.oneValueInputLayout.setHint(hint)

        oldValue.let { if (!it.isNullOrEmpty()) binding.newValueEt.setText(it.toString()) }

        val listener = DialogInterface.OnClickListener { _, i ->
            when (i) {
                DialogInterface.BUTTON_NEGATIVE -> this.dismiss()
                DialogInterface.BUTTON_POSITIVE -> {
                    val newValue = binding.newValueEt.text.toString()
                    setFragmentResult(FragConstants.NEW_VALUE, bundleOf(typeOfValue.toString() to newValue))
                }
            }
        }

        val builder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Save", listener)
            .setNegativeButton("Cancel", listener)
            .create()
        return builder
    }
}