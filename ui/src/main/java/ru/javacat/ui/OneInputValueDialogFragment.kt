package ru.javacat.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.ui.databinding.FragmentOneInputValueDialogBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class OneInputValueDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOneInputValueDialogBinding
    private var oldValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOneInputValueDialogBinding.inflate(layoutInflater)
        oldValue = arguments?.getString(FragConstants.DOCS_NUMBER)
        println("Старое ЗНАЧ = $oldValue")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val typeOfValue = arguments?.getString(FragConstants.TYPE_OF_VALUE)
//
//        val hint = when (typeOfValue) {
//            FragConstants.DAYS_TO_PAY -> "Срок оплаты, дней"
//            FragConstants.PRICE -> "Цена, руб."
//            FragConstants.DOCS_NUMBER -> "Номер отправления"
//            else -> "Новое Значение"
//        }
        binding.oneValueInputLayout.setHint("Введите Номер отправления")
        binding.newValueEt.requestFocus()

        oldValue?.let {
            binding.newValueEt.setText(it.toString()) }

        binding.saveBtn.setOnClickListener {
            val newValue = binding.newValueEt.text.toString()
            setFragmentResult(FragConstants.NEW_VALUE, bundleOf(FragConstants.DOCS_NUMBER to newValue))
            this.dismiss()
        }

        binding.cancelBtn.setOnClickListener {
            this.dismiss()
        }

    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val binding = FragmentOneInputValueDialogBinding.inflate(layoutInflater)
//
//
//
//
//
//        //Toast.makeText(requireContext(), "oldValue: ${oldValue.toString()}", Toast.LENGTH_SHORT).show()
//
//        //typeOfValue.let { Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show() }
//        //TODO посмотреть по аргументам, прилетают нули если не обновлял поле
//
//
//
//
//        //TODO разобратсья с типами, для документов стринг должен быть или лонг!
//
//        val listener = DialogInterface.OnClickListener { _, i ->
//            when (i) {
//                DialogInterface.BUTTON_NEGATIVE -> this.dismiss()
//                DialogInterface.BUTTON_POSITIVE -> {
//                    val newValue = binding.newValueEt.text.toString()
//                    setFragmentResult(FragConstants.NEW_VALUE, bundleOf(typeOfValue.toString() to newValue))
//                }
//            }
//        }
//
//        val builder = AlertDialog.Builder(requireContext())
//            .setView(binding.root)
//            .setPositiveButton("Save", listener)
//            .setNegativeButton("Cancel", listener)
//            .create()
//        return builder
//    }
}