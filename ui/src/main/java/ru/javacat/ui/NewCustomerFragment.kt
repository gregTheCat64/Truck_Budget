package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import ru.javacat.common.utils.toBase64
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee
import ru.javacat.ui.adapters.EmployeesAdapter
import ru.javacat.ui.adapters.OnEmployeeListener
import ru.javacat.ui.databinding.FragmentNewCustomerBinding
import ru.javacat.ui.utils.AndroidUtils
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.NewCustomerViewModel

@AndroidEntryPoint
class NewCustomerFragment:BaseFragment<FragmentNewCustomerBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    private var customerId: Long? = null

    private val viewModel: NewCustomerViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewCustomerBinding
        get() = { inflater, container ->
            FragmentNewCustomerBinding.inflate(inflater, container, false)
        }

    private var id: Long = 0L
    private var companyName: String = ""
    private var atiNumber: Int? = null
    private var telNumber: String? = null
    private var formalAddress: String? = null
    private var postAddress: String? = null
    private var shortName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerId = arguments?.getLong(FragConstants.CUSTOMER_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)
        (activity as AppCompatActivity). supportActionBar?.title = "Новый клиент"

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
                        saveCustomer(customerId?:0L)
                        findNavController().navigateUp()
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

        customerId = arguments?.getLong(FragConstants.CUSTOMER_ID)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                if (customerId != null) {
                    viewModel.getCustomerById(customerId!!)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedCustomer.collectLatest {
                    it?.let { updateUi(it) }
                }
            }
        }

        binding.saveBtn.setOnClickListener {
            getFieldsData()
            saveCustomer(customerId?:0L)
            findNavController().navigateUp()
        }

        binding.name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrBlank()) {
                    binding.nameLayout.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })


    }

    private fun updateUi(customer: Customer){
        binding.apply {
            (activity as AppCompatActivity).supportActionBar?.title = "Редактирование"
            name.setText(customer.name)
            atiNumber.setText(customer.atiNumber.toString())
            phoneNumber.setText(customer.companyPhone)
            legalAddress.setText(customer.formalAddress)
            postAddress.setText(customer.postAddress)
            shortName.setText(customer.shortName)
        }
    }


    private fun getFieldsData() {
        val requestField = "Обязательное поле"

        atiNumber = binding.atiNumber.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        }

        if (binding.name.text.isNullOrEmpty()) {
            binding.nameLayout.error = requestField
            binding.name.requestFocus()
            return
        } else {
            companyName = formatName(binding.name.text.toString())

        }

        telNumber = binding.phoneNumber.text?.let {
            if (it.isBlank()) null else it.toString()
        }

        formalAddress = binding.legalAddress.text?.let {
            if (it.isBlank()) null else it.toString()
        }

        postAddress = binding.postAddress.text?.let {
            if (it.isBlank()) null else it.toString()
        }


        if (binding.shortName.text.isNullOrEmpty()) {
            shortName = toShortName(companyName)
        } else shortName = binding.shortName.text.toString()
    }

    private fun saveCustomer(id: Long) {
            val newCustomer = Customer(
                id, companyName, atiNumber, emptyList(), telNumber, formalAddress, postAddress, shortName
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
