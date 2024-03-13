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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.ChooseCustomerAdapter
import ru.javacat.ui.adapters.ChooseDriverAdapter
import ru.javacat.ui.adapters.ChooseTrailerAdapter
import ru.javacat.ui.adapters.ChooseTruckAdapter
import ru.javacat.ui.adapters.CustomersAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.view_models.ChooseItemViewModel

@AndroidEntryPoint
class ChooseItemFragment : BaseFragment<FragmentChooseItemBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentChooseItemBinding
        get() = { inflater, container ->
            FragmentChooseItemBinding.inflate(inflater, container, false)
        }

    private val viewModel: ChooseItemViewModel by viewModels()
    private lateinit var trailersAdapter: ChooseTrailerAdapter
    private lateinit var trucksAdapter: ChooseTruckAdapter
    private lateinit var driversAdapter: ChooseDriverAdapter
    private lateinit var customersAdapter: ChooseCustomerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments

        when (val requestedItem = args?.getString("item","none")?:"unknown") {
            "DRIVER" -> {
                initDriversAdapter()
            }
            "TRUCK" -> {
                initTrucksAdapter(requestedItem)
            }
            "TRAILER" -> {
                initTrailersAdapter(requestedItem)
            }
//            "CUSTOMER" -> {
//                initCustomerAdapter()
//            }
            "CARGO" -> {
                initCargoAdapter()
            }

            else -> {
                println("Unknown item")
            }
        }


    }

    private fun initTrucksAdapter(requestedItem: String){
        val bundle = Bundle()
        bundle.putString("item", requestedItem)
        binding.newItemBtn.setOnClickListener {
            findNavController().navigate(R.id.newTransportFragment, bundle)
        }
        viewModel.getTrucks()
        trucksAdapter = ChooseTruckAdapter {
            Log.i("ChoseItemFrag", "changing Truck")
           viewModel.setTruck(it)
            findNavController().navigateUp()
        }
        binding.itemList.adapter = trucksAdapter

        binding.itemNameTextView.text = "Тягачи"

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.editedRoute.collectLatest {
                    binding.searchView.setText(it.truck?.regNumber)

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trucks.collectLatest {
                    trucksAdapter.submitList(it)
                }
            }
        }
    }

    private fun initTrailersAdapter(requestedItem: String){
        val bundle = Bundle()
        bundle.putString("item", requestedItem)

        binding.newItemBtn.setOnClickListener {
            findNavController().navigate(R.id.newTransportFragment, bundle)
        }
        viewModel.getTrailers()
        trailersAdapter = ChooseTrailerAdapter {
           viewModel.setTrailer(it)
            findNavController().navigateUp()
        }
        binding.itemList.adapter = trailersAdapter

        binding.itemNameTextView.text = "Прицепы"

//        viewLifecycleOwner.lifecycleScope.launch{
//            viewModel.chosenTrailer.collectLatest {
//                if (it != null){
//                    binding.searchView.setText(it?.regNumber)
//
//                }
//
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.editedRoute.collectLatest {
                binding.searchView.setText(it.trailer?.regNumber)

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trailers.collectLatest {
                    trailersAdapter.submitList(it)
                }
            }
        }
    }

    private fun initDriversAdapter(){
        binding.newItemBtn.setOnClickListener {
            findNavController().navigate(R.id.newDriverFragment)
        }
        viewModel.getDriver()
        driversAdapter = ChooseDriverAdapter {
           viewModel.setDriver(it)
            findNavController().navigateUp()
        }
        binding.itemList.adapter = driversAdapter

        binding.itemNameTextView.text = "Водители"

//        viewLifecycleOwner.lifecycleScope.launch{
//            viewModel.chosenDriver.collectLatest {
//                if (it != null){
//                    binding.searchView.setText(it?.fullName)
//
//                }
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.editedRoute.collectLatest {
                binding.searchView.setText(it.driver?.fullName)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.drivers.collectLatest {
                    driversAdapter.submitList(it)
                }
            }
        }
    }

//    private fun initCustomerAdapter(){
//        viewModel.getCustomer()
//        customersAdapter = ChooseCustomerAdapter {
//            viewModel.setCustomer(it)
//            findNavController().navigateUp()
//        }
//        binding.itemList.adapter = customersAdapter
//        binding.itemNameTextView.text = "Клиенты"
//
//        binding.newItemBtn.setOnClickListener {
//            findNavController().navigate(R.id.newCustomerFragment)
//        }
//
//        viewLifecycleOwner.lifecycleScope.launch{
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                viewModel.customers.collectLatest {
//                    customersAdapter.submitList(it)
//                }
//            }
//        }
//    }

    private fun initCargoAdapter(){

    }
}