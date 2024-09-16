package ru.javacat.ui.TruckDriver

import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.TruckDriver
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentDriverInfoBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class TruckDriverInfoFragment: BaseFragment<FragmentDriverInfoBinding>() {
    private val viewModel: TruckDriverInfoViewModel by viewModels()

    private var truckDriverId: Long? = null
    private var companyId: Long = -1L

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentDriverInfoBinding
        get() = {inflater, container ->
            FragmentDriverInfoBinding.inflate(inflater, container, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        truckDriverId = args?.getLong(FragConstants.DRIVER_ID)?:0L
        companyId = args?.getLong(FragConstants.COMPANY_ID)?:-1L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

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
                        editTruckDriver()
                        return true
                    }

                    R.id.remove_menu_item -> {
                        truckDriverId?.let { hideDriver(it) }
                        return true
                    }

                    else ->  return false
                }
            }
        }, viewLifecycleOwner)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        truckDriverId?.let { viewModel.getTruckDriver(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedTruckDriver.collectLatest {
                    if (it != null) {
                        updateUi(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    when (it) {
                        is LoadState.Success.Created -> {
                            findNavController().navigateUp()
                            Toast.makeText(requireContext(),
                                getString(R.string.created), Toast.LENGTH_SHORT).show()
                        }
                        is LoadState.Loading -> {
                            binding.progressBar.isGone = false
                        }
                        is LoadState.Success.Removed -> {
                            findNavController().navigateUp()
                            Toast.makeText(requireContext(), getString(R.string.removed), Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun updateUi(td: TruckDriver){
        (activity as AppCompatActivity).supportActionBar?.title = td.nameToShow

        val name = "${td.surname} ${td.middleName?:""} ${td.firstName?:""}"
        val passport = "${td.passportNumber?:""} ${td.passportReceivedPlace?:""} ${td.passportReceivedDate?:""}"
        val driveLicense = td.driveLicenseNumber?:""
        val address = td.placeOfRegistration?:""
        val phone = td.phoneNumber?:""
        val phone2 = td.secondNumber?:""
        val comment = td.comment?:""

        binding.apply {
            driverFullNameValue.text = name
            passportValue.text = passport
            driverLicenseValue.text = driveLicense
            addressValue.text = address
            phoneValue.text = phone
            phone2Value.text = phone2
            commentValue.text = comment
        }
    }

    private fun editTruckDriver(){
        if (truckDriverId!= null){
            val bundle = Bundle()
            bundle.putLong(FragConstants.COMPANY_ID, companyId)
            bundle.putLong(FragConstants.DRIVER_ID, truckDriverId!!)
            findNavController().navigate(R.id.action_truckDriverInfoFragment_to_newDriverFragment, bundle)
        } else Toast.makeText(requireContext(), "Something wrong, td id: $truckDriverId", Toast.LENGTH_SHORT).show()
    }

    private fun hideDriver(id: Long){
        Log.i("NewDriverFrag", "driverId: $id")
        viewModel.hideDriver(id)

    }
}