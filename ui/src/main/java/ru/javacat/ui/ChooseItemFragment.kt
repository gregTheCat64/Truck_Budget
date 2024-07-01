package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.ChooseCompanyAdapter
import ru.javacat.ui.adapters.ChooseDriverAdapter
import ru.javacat.ui.adapters.ChooseTrailerAdapter
import ru.javacat.ui.adapters.ChooseTruckAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.ChooseItemViewModel

@AndroidEntryPoint
class ChooseItemFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseItemBinding
    private val viewModel: ChooseItemViewModel by viewModels()

    private lateinit var trailersAdapter: ChooseTrailerAdapter
    private lateinit var trucksAdapter: ChooseTruckAdapter
    private lateinit var driversAdapter: ChooseDriverAdapter
    private lateinit var companiesAdapter: ChooseCompanyAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments

        val companyId = args?.getLong(FragConstants.COMPANY_ID)?:-1L

        when (val requestedItem = args?.getString("item","none")?:"unknown") {
            "DRIVER" -> {
                initDriversCase(companyId)
            }
            "TRUCK" -> {
                initTrucksCase(requestedItem, companyId)
            }
            "TRAILER" -> {
                initTrailersCase(requestedItem, companyId)
            }
            "CONTRACTOR" -> {
                initContractorsCase()
            }

            else -> {
                println("Unknown item")
            }
        }
    }

    private fun initTrucksCase(requestedItem: String, companyId: Long){
        val bundle = Bundle()
        bundle.putLong(FragConstants.COMPANY_ID, companyId)
        bundle.putString(FragConstants.TYPE_OF_TRANSPORT, requestedItem)
        bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)

        binding.newItemBtn.setText(getString(R.string.create_new_truck))

        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            findNavController().navigate(R.id.newTransportFragment, bundle)
        }
        viewModel.getTrucks(companyId)
        trucksAdapter = ChooseTruckAdapter {
            Log.i("ChoseItemFrag", "changing Truck")
           viewModel.setTruck(it)
            this.dismiss()
            //findNavController().navigateUp()
        }
        binding.itemList.adapter = trucksAdapter

        binding.itemNameTextView.text = "Тягачи"

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trucks.collectLatest {
                    trucksAdapter.submitList(it)
                }
            }
        }
    }

    private fun initTrailersCase(requestedItem: String, companyId: Long){
        val bundle = Bundle()
        bundle.putLong(FragConstants.COMPANY_ID, companyId)
        bundle.putString(FragConstants.TYPE_OF_TRANSPORT, requestedItem)
        bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)

        binding.newItemBtn.text = getString(R.string.create_new_trailer)

        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            findNavController().navigate(R.id.newTransportFragment, bundle)
        }
        viewModel.getTrailers(companyId)

        trailersAdapter = ChooseTrailerAdapter {
           viewModel.setTrailer(it)
            this.dismiss()
            //findNavController().navigateUp()
        }
        binding.itemList.adapter = trailersAdapter

        binding.itemNameTextView.text = "Прицепы"


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trailers.collectLatest {
                    trailersAdapter.submitList(it)
                    //binding.searchView.isGone = it.isNullOrEmpty()
                }
            }
        }
    }

    private fun initDriversCase(companyId: Long){
        val bundle = Bundle()
        bundle.putLong(FragConstants.COMPANY_ID, companyId)
        bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)

        binding.newItemBtn.text = getString(R.string.create_new_truck_driver)
        binding.newItemBtn.setOnClickListener {
            this.dismiss()

            findNavController().navigate(R.id.newDriverFragment, bundle)
        }
        viewModel.getDriver(companyId)
        driversAdapter = ChooseDriverAdapter {
            viewModel.setDriver(it)
            this.dismiss()
            //findNavController().navigateUp()
        }
        binding.itemList.adapter = driversAdapter

        binding.itemNameTextView.text = "Водители"


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.drivers.collectLatest {
                    driversAdapter.submitList(it)
                    //binding.searchView.isGone = it.isNullOrEmpty()
                }
            }
        }
    }

    private fun initContractorsCase(){

        binding.newItemBtn.text = getString(R.string.create_new_partner)
        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            val bundle = Bundle()
            bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)
            findNavController().navigate(R.id.newCustomerFragment, bundle)
        }
        viewModel.getContractors()
        companiesAdapter = ChooseCompanyAdapter {
            viewModel.setCompany(it)
            this.dismiss()
            //findNavController().navigateUp()
        }
        binding.itemList.adapter = companiesAdapter

        binding.itemNameTextView.text = "Компании"

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.contractors.collectLatest {
                    companiesAdapter.submitList(it)
                    //binding.searchView.isGone = it.isNullOrEmpty()
                }
            }
        }
    }

}