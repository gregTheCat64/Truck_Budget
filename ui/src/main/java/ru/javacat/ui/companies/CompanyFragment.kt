package ru.javacat.ui.companies

import android.os.Bundle
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
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Manager
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.adapters.ManagerAdapter
import ru.javacat.ui.adapters.OnManagerListener
import ru.javacat.ui.databinding.FragmentCompanyBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.makePhoneCall
import ru.javacat.ui.utils.sendMessageToWhatsApp

@AndroidEntryPoint
class CompanyFragment: BaseFragment<FragmentCompanyBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    private var customerId: Long? = null
    private lateinit var emplAdapter: ManagerAdapter
    private val viewModel: CompanyViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentCompanyBinding
        get() = { inflater, container ->
            FragmentCompanyBinding.inflate(inflater, container, false)
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

                    R.id.edit_menu_item -> {
                        val bundle = Bundle()
                        if (customerId != null) {
                            bundle.putLong(FragConstants.CUSTOMER_ID, customerId!!)
                            findNavController().navigate(R.id.action_companyFragment_to_newCustomerFragment, bundle)
                        }
                        return true
                    }
                    R.id.remove_menu_item -> {
                        customerId?.let { removeCompany(it) }
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

        binding.callCompanyPhoneBtn.setOnClickListener {
            binding.phoneNumberTv.text?.let {
                if (it.isNotEmpty()) makePhoneCall(it.toString())
            }
        }

        binding.whatsappCompanyMsgBtn.setOnClickListener {
            binding.phoneNumberTv.text?.let {
                if (it.isNotEmpty()) sendMessageToWhatsApp(requireContext(), it.toString(), "")
            }
        }

        binding.toTruckFleet.setOnClickListener {
            if (customerId != null) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.CUSTOMER_ID, customerId!!)
                findNavController().navigate(R.id.action_companyFragment_to_truckFleetViewPager, bundle)
            }
        }

        binding.addEmployeeBtn.setOnClickListener {
            if (customerId != null) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.COMPANY_ID,customerId!!)
                findNavController().navigate(R.id.action_companyFragment_to_newEmployeeFragment, bundle)
            }
        }

        emplAdapter = ManagerAdapter(object : OnManagerListener{
            override fun onManager(item: Manager) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.MANAGER_ID, item.id)
                findNavController().navigate(R.id.action_companyFragment_to_newEmployeeFragment, bundle)
            }

            override fun onPhone(item: String?) {
                if (item != null) {
                    makePhoneCall(item)
                } else Toast.makeText(requireContext(), "Номер не найден", Toast.LENGTH_SHORT).show()
            }

            override fun onWhatsapp(item: String?) {
                if (item != null) {
                    sendMessageToWhatsApp(requireContext(), item, "")
                } else Toast.makeText(requireContext(), "Номер не найден", Toast.LENGTH_SHORT).show()
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
            companyPhoneLayout.isGone = customer.companyPhone==null
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

    private fun removeCompany(id: Long){
        viewModel.hideCompanyById(id)
    }
}