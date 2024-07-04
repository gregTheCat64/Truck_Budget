package ru.javacat.ui

import android.os.Bundle
import android.util.Log
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
import ru.javacat.ui.databinding.FragmentHomeCompanyBinding

import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.HomeFragmentViewModel

@AndroidEntryPoint
class HomeCompanyFragment: BaseFragment<FragmentHomeCompanyBinding>() {
    private val viewModel: HomeFragmentViewModel by viewModels()

    var customerId: Long? = null

    override var bottomNavViewVisibility: Int = View.VISIBLE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentHomeCompanyBinding
        get() = {inflater, container ->
            FragmentHomeCompanyBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        viewModel.getHomeCustomer()
        customerId = FragConstants.MY_COMPANY_ID

        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)


        requireActivity().addMenuProvider(object : MenuProvider {
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
                        Log.i("HomeFrag", "customerId =  $customerId")
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.homeCustomer.collectLatest {
                    it?.let { updateUi(it)}
                }
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toTruckDriversBtn.setOnClickListener {
            findNavController().navigate(R.id.truckDriversListFragment)
        }
    }

    private fun updateUi(customer: Company){
        binding.apply {
            customer.nameToShow.let {
                customerNameTv.text = it
            }
            customer.shortName?.let {
                shortNameTv.text = it
            }
            customer.atiNumber?.let {
                atiNumberTv.text = it.toString()
            }
            customer.companyPhone?.let {
                phoneNumberTv.text = it
            }
            customer.formalAddress?.let {
                formalAddressTv.text = it
            }
            customer.postAddress?.let {
                postAddressTv.text = it
            }
        }
    }
}