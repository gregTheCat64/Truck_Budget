package ru.javacat.ui.new_route

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
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.adapters.ChooseCustomerAdapter
import ru.javacat.ui.adapters.ChooseManagerChipAdapter
import ru.javacat.ui.databinding.FragmentAddCustomerBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.FragConstants.IS_NEW_ORDER

@AndroidEntryPoint
class AddCustomerFragment : BaseFragment<FragmentAddCustomerBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddCustomerBinding
        get() = { inflater, container ->
            FragmentAddCustomerBinding.inflate(inflater, container, false)
        }

    private val viewModel: AddCustomerViewModel by viewModels()
    private lateinit var customerAdapter: ChooseCustomerAdapter
    private lateinit var employeeAdapter: ChooseManagerChipAdapter

    private var isNewOrder = true

    private var customerId = 0L
    private var routeId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        isNewOrder = args?.getBoolean(IS_NEW_ORDER) ?: true
        Log.i("AddCustomFrag", "isNewOrder: $isNewOrder")
        routeId = args?.getLong(FragConstants.ROUTE_ID)?:0L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity). supportActionBar?.show()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_cancel, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    R.id.cancel_button_menu_item -> {
                        if (isNewOrder){
                            findNavController().popBackStack(R.id.routeViewPagerFragment, false)
                        } else findNavController().popBackStack(R.id.editOrderFragment, false)
                        return true
                    }
                    else -> return false
                }
            }

        }, viewLifecycleOwner)


        initUi()
        initCustomerAdapter()
        initEmployeeAdapter()
        addCustomerEditTextListener()
        addManagerEditTextListener()
        loadStateListener()
        

        binding.newCustomerBtn.setOnClickListener {
            findNavController().navigate(R.id.newCustomerFragment)
        }

        binding.newManagerBtn.setOnClickListener {
            val bundle = Bundle()
            if (customerId != 0L){
                bundle.putLong(FragConstants.CUSTOMER_ID, customerId)
                findNavController().navigate(R.id.newEmployeeFragment, bundle)
            }
        }

        binding.okBtn.setOnClickListener {
            if (!binding.customerInputEditText.text.isNullOrEmpty()){
                viewModel.addCustomerToOrder(routeId)
            } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()

        }
    }

    private fun initUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    order?.customer?.let {
                        binding.customerInputEditText.setText(it.nameToShow)
                    }
                    order?.manager?.let {
                        binding.managerInputEditText.setText(it.nameToShow)
                    }
                }
            }
        }
    }

    private fun initCustomerAdapter() {
        viewModel.getCustomers()
        customerAdapter = ChooseCustomerAdapter {
            viewModel.setCustomer(it)
            viewModel.clearEmployee()
            binding.managerInputEditText.text?.clear()
            binding.customerInputEditText.setText(it.nameToShow)
            customerId = it.id
            viewModel.getEmployee(it.id)
            binding.managerLayout.isGone = false
            binding.customersRecView.isGone = true
        }
        binding.customersRecView.adapter = customerAdapter

        //val decoration = DividerItemDecoration(context, RecyclerView.HORIZONTAL)
        // decoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_event_note_24)!!)
        //binding.customersRecView.addItemDecoration(decoration)
        val customersLayoutManager = FlexboxLayoutManager(requireContext())
        customersLayoutManager.flexDirection = FlexDirection.ROW
        customersLayoutManager.justifyContent = JustifyContent.FLEX_START
        binding.customersRecView.layoutManager = customersLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.customers.collectLatest {
                    customerAdapter.submitList(it)
                }
            }
        }
    }

    private fun initEmployeeAdapter() {
        employeeAdapter = ChooseManagerChipAdapter {
            viewModel.setEmployee(it)
            binding.managerInputEditText.setText(it.nameToShow)
        }
        binding.employeesRecView.adapter = employeeAdapter

        val employeesLayoutManager = FlexboxLayoutManager(requireContext())
        employeesLayoutManager.flexDirection = FlexDirection.ROW
        employeesLayoutManager.justifyContent = JustifyContent.FLEX_START
        binding.employeesRecView.layoutManager = employeesLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.managers.collectLatest {
                    employeeAdapter.submitList(it)
                }
            }
        }
    }

    private fun addCustomerEditTextListener() {
        binding.customerInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchCustomers(p0.toString())
                binding.managerLayout.isGone = p0.isNullOrEmpty()
                binding.customersRecView.isGone = false
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun addManagerEditTextListener() {
        binding.managerInputEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchEmployee(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun loadStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadState.collectLatest {
                    if (it is LoadState.Success.OK) {
                        if (isNewOrder) {
                            findNavController().navigate(R.id.addCargoFragment)
                        } else findNavController().popBackStack(R.id.editOrderFragment, false)
                    }
                    if (it is LoadState.Error) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.fill_requested_fields),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}