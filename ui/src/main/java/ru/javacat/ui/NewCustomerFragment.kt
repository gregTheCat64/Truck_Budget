package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.toBase64
import ru.javacat.domain.models.Customer
import ru.javacat.ui.adapters.EmployeesAdapter
import ru.javacat.ui.databinding.FragmentNewCustomerBinding
import ru.javacat.ui.utils.AndroidUtils
import ru.javacat.ui.view_models.NewCustomerViewModel

@AndroidEntryPoint
class NewCustomerFragment:BaseFragment<FragmentNewCustomerBinding>() {


    private val viewModel: NewCustomerViewModel by viewModels()
    private lateinit var adapter: EmployeesAdapter
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewCustomerBinding
        get() = { inflater, container ->
            FragmentNewCustomerBinding.inflate(inflater, container, false)
        }

    private var id: Int = 0
    private var companyName: String = ""
    private var atiNumber: Int? = null
    private var telNumber: String? = null
    private var formalAddress: String? = null
    private var postAddress: String? = null
    private var shortName: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EmployeesAdapter()
        binding.employeesRecView.adapter = adapter


        lifecycleScope.launch {
            viewModel.employees.collectLatest {
                adapter.submitList(it)
            }
        }

        binding.saveBtn.setOnClickListener {
            getFieldsData()
            saveCustomer()
            findNavController().navigateUp()
        }

        binding.companyName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrBlank()) {
                    binding.companyNameLayout.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.addEmployeeBtn.setOnClickListener {
            getFieldsData()
            saveCustomer()

            val bundle = Bundle()
//            if (id.isNotEmpty()){
//                bundle.putString("id", id)
//                findNavController().navigate(R.id.newEmployeeFragment, bundle)
//            }
        }
    }


    private fun getFieldsData() {
        val requestField = "Обязательное поле"

        atiNumber = binding.atiNumber.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        }

        if (binding.companyName.text.isNullOrEmpty()) {
            binding.companyNameLayout.error = requestField
            binding.companyName.requestFocus()
            return
        } else {
            companyName = formatName(binding.companyName.text.toString())

//            id = if (atiNumber != null) {
//                atiNumber.toString()
//            } else {
//                companyName.toBase64()
//            }
        }

        telNumber = binding.phoneNumber.text?.let {
            if (it.isBlank()) null else it.toString()
        }

        formalAddress = binding.formalAddress.text?.let {
            if (it.isBlank()) null else it.toString()
        }

        postAddress = binding.postAddress.text?.let {
            if (it.isBlank()) null else it.toString()
        }


        if (binding.shortName.text.isNullOrEmpty()) {
            shortName = toShortName(companyName)
        } else shortName = binding.shortName.text.toString()
    }

    private fun saveCustomer() {

            val newCustomer = Customer(
                null, companyName, atiNumber, telNumber, formalAddress, postAddress, shortName
            )
            AndroidUtils.hideKeyboard(requireView())
            viewModel.saveNewCustomer(newCustomer)

    }

    private fun formatName(str: String): String {
        return str
            .replace("Ооо", "ООО")
            .replace("Ип", "ИП")
            .trim()
    }

    private fun toShortName(str: String): String {
        return str
            .replace("ООО", "")
            .replace("Ооо", "")
            .replace("ИП", "")
            .replace("Ип", "")
            .replace("\"", "")
    }
}

//    override fun onResume() {
//        super.onResume()
//        //if (id.isNotEmpty()) viewModel.getEmployeeListByCustomerId(id)
//    }
