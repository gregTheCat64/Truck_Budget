package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.ChooseDriverAdapter
import ru.javacat.ui.adapters.ChooseTrailerAdapter
import ru.javacat.ui.adapters.ChooseTruckAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.view_models.ChooseItemViewModel

@AndroidEntryPoint
class ChooseItemFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseItemBinding
    private val viewModel: ChooseItemViewModel by viewModels()
    private lateinit var trailersAdapter: ChooseTrailerAdapter
    private lateinit var trucksAdapter: ChooseTruckAdapter
    private lateinit var driversAdapter: ChooseDriverAdapter
    //private lateinit var customersAdapter: ChooseCustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments

        when (val requestedItem = args?.getString("item","none")?:"unknown") {
            "DRIVER" -> {
                initDriversCase()
            }
            "TRUCK" -> {
                initTrucksCase(requestedItem)
            }
            "TRAILER" -> {
                initTrailersCase(requestedItem)
            }

            else -> {
                println("Unknown item")
            }
        }
    }

    private fun initTrucksCase(requestedItem: String){
        val bundle = Bundle()
        bundle.putString("item", requestedItem)

        binding.newItemBtn.setText(getString(R.string.create_new_truck))

        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            findNavController().navigate(R.id.newTransportFragment, bundle)
        }
        viewModel.getTrucks()
        trucksAdapter = ChooseTruckAdapter {
            Log.i("ChoseItemFrag", "changing Truck")
           viewModel.setTruck(it)
            this.dismiss()
            //findNavController().navigateUp()
        }
        binding.itemList.adapter = trucksAdapter

        binding.itemNameTextView.text = "Тягачи"

//        viewLifecycleOwner.lifecycleScope.launch{
//            viewModel.editedRoute.collectLatest {
//                    binding.searchView.setText(it?.truck?.regNumber)
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trucks.collectLatest {
                    trucksAdapter.submitList(it)
                    //binding.searchView.isGone = it.isNullOrEmpty()
                }
            }
        }
    }

    private fun initTrailersCase(requestedItem: String){
        val bundle = Bundle()
        bundle.putString("item", requestedItem)

        binding.newItemBtn.text = getString(R.string.create_new_trailer)

        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            findNavController().navigate(R.id.newTransportFragment, bundle)
        }
        viewModel.getTrailers()

        trailersAdapter = ChooseTrailerAdapter {
           viewModel.setTrailer(it)
            this.dismiss()
            //findNavController().navigateUp()
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

//        viewLifecycleOwner.lifecycleScope.launch{
//            viewModel.editedRoute.collectLatest {
//                binding.searchView.setText(it?.trailer?.regNumber)
//
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trailers.collectLatest {
                    trailersAdapter.submitList(it)
                    //binding.searchView.isGone = it.isNullOrEmpty()
                }
            }
        }
    }

    private fun initDriversCase(){

        binding.newItemBtn.text = getString(R.string.create_new_truck_driver)
        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            findNavController().navigate(R.id.newDriverFragment)
        }
        viewModel.getDriver()
        driversAdapter = ChooseDriverAdapter {
            viewModel.setDriver(it)
            this.dismiss()
            //findNavController().navigateUp()
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

//        viewLifecycleOwner.lifecycleScope.launch{
//            viewModel.editedRoute.collectLatest {
//                binding.searchView.setText(it?.driver?.surname)
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.drivers.collectLatest {
                    driversAdapter.submitList(it)
                    //binding.searchView.isGone = it.isNullOrEmpty()
                }
            }
        }
    }

}