package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.domain.models.Employee
import ru.javacat.ui.databinding.FragmentNewEmployeeBinding
import ru.javacat.ui.view_models.NewEmployeeViewModel

@AndroidEntryPoint
class NewEmployeeFragment: BaseFragment<FragmentNewEmployeeBinding>() {

    private val viewModel: NewEmployeeViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewEmployeeBinding =
        {inflater, container ->
            FragmentNewEmployeeBinding.inflate(inflater, container, false)
        }

    private var name: String = ""
    private var phoneNumber: String = ""
    private var secondPhoneNumber: String? = null
    private var email:String? = null
    private var comment:String? = null
    private var customerId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        customerId = args?.getString("id")?:""


        binding.saveBtn.setOnClickListener {
            getFieldsData()
            saveEmployee()
        }

        binding.name.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrBlank()){
                    binding.nameLayout.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.phoneNumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrBlank()){
                    binding.phoneLayout.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun getFieldsData(){
        val requestField = "Обязательное поле"
        if (binding.name.text.isNullOrEmpty()){
            binding.nameLayout.error = requestField
            binding.name.requestFocus()
            return
        } else {
            name = binding.name.text.toString()
        }

        if (binding.phoneNumber.text.isNullOrEmpty()){
            binding.phoneLayout.error = requestField
            binding.phoneNumber.requestFocus()
            return
        } else {
            phoneNumber = binding.phoneNumber.text.toString()
        }

        secondPhoneNumber = binding.phoneNumber2.text?.let {
            if (it.isBlank()) null else it.toString()
        }

        email = binding.email.text?.let {
            if (it.isBlank()) null else it.toString()
        }

        comment = binding.comment.text?.let {
            if (it.isBlank()) null else it.toString()
        }
    }
    
    private fun saveEmployee(){
        if (name.isNotEmpty() && phoneNumber.isNotEmpty() && customerId.isNotEmpty()){
            val newEmployee = Employee(
                0 , customerId, name, phoneNumber, secondPhoneNumber, email, comment
            )
            viewModel.saveNewEmployee(newEmployee)
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Заполните необходимые поля", Toast.LENGTH_SHORT).show()
        }
    }

}