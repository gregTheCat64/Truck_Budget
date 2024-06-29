package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Manager
import ru.javacat.ui.adapters.ManagerAdapter
import ru.javacat.ui.adapters.OnManagerListener
import ru.javacat.ui.databinding.FragmentCustomerBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.CustomerViewModel

@AndroidEntryPoint
class CustomerFragment: BaseFragment<FragmentCustomerBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    private var customerId: Long? = null
    private lateinit var emplAdapter: ManagerAdapter
    private val viewModel: CustomerViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentCustomerBinding
        get() = { inflater, container ->
            FragmentCustomerBinding.inflate(inflater, container, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customerId = arguments?.getLong(FragConstants.CUSTOMER_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_edit_remove, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }

                    R.id.edit -> {
                        val bundle = Bundle()
                        if (customerId != null) {
                            bundle.putLong(FragConstants.CUSTOMER_ID, customerId!!)
                            findNavController().navigate(R.id.newCustomerFragment, bundle)
                        }
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

        binding.toTruckFleet.setOnClickListener {
            if (customerId != null) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.CUSTOMER_ID, customerId!!)
                findNavController().navigate(R.id.truckFleetViewPager, bundle)
            }
        }

        binding.addEmployeeBtn.setOnClickListener {
            if (customerId != null) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.CUSTOMER_ID,customerId!!)
                findNavController().navigate(R.id.newEmployeeFragment, bundle)
            }
        }

        emplAdapter = ManagerAdapter(object : OnManagerListener{
            override fun onManager(item: Manager) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.MANAGER_ID, item.id)
                findNavController().navigate(R.id.newEmployeeFragment, bundle)
            }
        })

        binding.employeesRV.adapter = emplAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                customerId?.let { viewModel.getCustomerById(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedCustomer.collectLatest {customer->
                    customer?.let {
                        updateUi(it)
                    }
                }
            }
        }
    }

    private fun updateUi(customer: Company){
        binding.apply {
            (activity as AppCompatActivity).supportActionBar?.title = customer.shortName
            customerNameTv.text = customer.nameToShow
            shortNameTv.text = customer.shortName
            customer.atiNumber?.let {
                atiNumberTv.text = it.toString()
            }
            customer.companyPhone?.let {
                phoneNumberTv.text = it
            }

            formalAddressTv.text = customer.formalAddress
            postAddressTv.text = customer.postAddress
        }

        customer.managers.let {
            emplAdapter.submitList(it)
        }

    }
}