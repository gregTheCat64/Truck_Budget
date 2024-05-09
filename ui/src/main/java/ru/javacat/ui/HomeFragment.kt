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
import ru.javacat.domain.models.Customer
import ru.javacat.ui.databinding.FragmentHomeBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.HomeFragmentViewModel

@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeFragmentViewModel by viewModels()

    var customerId: Long? = null

    override var bottomNavViewVisibility: Int = View.VISIBLE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentHomeBinding
        get() = {inflater, container ->
            FragmentHomeBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Моя компания"

        viewModel.getHomeCustomer()

        customerId = -1

        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)


        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_review, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }

                    R.id.edit -> {
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

    private fun updateUi(customer: Customer){
        binding.apply {
            customerNameTv.setText(customer.name)
            //innTv.setText(customer.id.toString())
            atiNumberTv.setText(customer.atiNumber.toString())
            phoneNumberTv.setText(customer.companyPhone.toString())
        }
    }
}