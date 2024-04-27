package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Employee
import ru.javacat.ui.databinding.FragmentNewEmployeeBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.NewEmployeeViewModel

@AndroidEntryPoint
class NewEmployeeFragment: BaseFragment<FragmentNewEmployeeBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

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
    private var customerId: Long = 0L
    private var employeeId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        customerId = args?.getLong(FragConstants.CUSTOMER_ID)?:0
        Log.i("Log", "custmId: $customerId")

        //employeeId = null

        employeeId = args?.getLong(FragConstants.EMPLOYEE_ID)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)
        (activity as AppCompatActivity).supportActionBar?.title = "Новый сотрудник"

        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_edit, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }

                    R.id.save -> {
                        getFieldsData()
                        saveEmployee(employeeId?:0L)
                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                Log.i("NewEmployeeFrag", "emplID: ${employeeId.toString()}")
                if (employeeId!= null){
                    viewModel.getEmployeeById(employeeId!!)
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedEmployee.collectLatest {
                    if (it != null && employeeId != null) {
                        customerId = it.customerId
                        updateUi(it)
                    }
                }
            }
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

    private fun updateUi(employee: Employee){
        binding.apply {
            (activity as AppCompatActivity).supportActionBar?.title = employee.name
            name.setText(employee.name)
            phoneNumber.setText(employee.phoneNumber)
            phoneNumber2.setText(employee.secondNumber)
            email.setText(employee.email)
            comment.setText(employee.comment)
        }
    }
    
    private fun saveEmployee(id: Long){
        if (name.isNotEmpty() && phoneNumber.isNotEmpty() && customerId != 0L){
            val newEmployee = Employee(
                id , name, customerId,  phoneNumber, secondPhoneNumber, email, comment
            )
            viewModel.saveNewEmployee(newEmployee)
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), "Заполните необходимые поля", Toast.LENGTH_SHORT).show()
        }
    }

}