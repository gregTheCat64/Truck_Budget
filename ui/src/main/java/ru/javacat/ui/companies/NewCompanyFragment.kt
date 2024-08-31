package ru.javacat.ui.companies

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
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.formatCompanyName
import ru.javacat.common.utils.toShortCompanyName
import ru.javacat.domain.models.Company
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentNewCustomerBinding
import ru.javacat.ui.utils.AndroidUtils
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.NewCompanyViewModel

@AndroidEntryPoint
class NewCompanyFragment : BaseFragment<FragmentNewCustomerBinding>() {

    private var customerId: Long? = null
    private var isNeedToSet: Boolean = false

    private val viewModel: NewCompanyViewModel by viewModels()

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
        isNeedToSet = arguments?.getBoolean(FragConstants.IS_NEED_TO_SET) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)


        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_save, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }

                    R.id.save -> {
                        saveCustomer()
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (customerId != null) {
                    viewModel.getCustomerById(customerId!!)
                }
            }
        }

        //nav
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadState.collectLatest {
                    when (it) {
                        is LoadState.Success.GoBack -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.saved), Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }

                        is LoadState.Error -> {
                            Toast.makeText(requireContext(), "Some Error", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is LoadState.Loading -> {
                            binding.progressBar.isGone = false
                        }

                        else -> {
                            Toast.makeText(requireContext(), "Something wrong", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedCustomer.collectLatest {
                    it?.let { updateUi(it) }
                }
            }
        }

        binding.saveBtn.setOnClickListener {
            saveCustomer()

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

    private fun updateUi(customer: Company) {
        binding.apply {
            (activity as AppCompatActivity).supportActionBar?.title = "Редактирование"
            name.setText(customer.nameToShow)
            customer.atiNumber?.let {
                atiNumber.setText(it.toString())
            }
            customer.companyPhone?.let {
                phoneNumber.setText(it)
            }
            customer.formalAddress?.let {
                legalAddress.setText(it)
            }
            customer.postAddress?.let {
                postAddress.setText(it.toString())
            }
            customer.shortName?.let {
                shortName.setText(it.toString())
            }
        }
    }


    private fun getFieldsData(): Boolean {
        val requestField = "Обязательное поле"

        atiNumber = binding.atiNumber.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        }

        if (binding.name.text.isNullOrEmpty()) {
            binding.nameLayout.error = requestField
            binding.name.requestFocus()
            return false
        } else {
            companyName = binding.name.text.toString().formatCompanyName()

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
            shortName = companyName.toShortCompanyName()
        } else shortName = binding.shortName.text.toString()

        return true
    }

    private fun saveCustomer() {
        if (getFieldsData()) {
            val newCustomer = Company(
                customerId?:0,
                companyName,
                atiNumber,
                emptyList(),
                telNumber,
                formalAddress,
                postAddress,
                shortName
            )
            AndroidUtils.hideKeyboard(requireView())
            viewModel.saveNewCustomer(newCustomer, isNeedToSet)
        }
    }
}

//    override fun onResume() {
//        super.onResume()
//        //if (id.isNotEmpty()) viewModel.getEmployeeListByCustomerId(id)
//    }
