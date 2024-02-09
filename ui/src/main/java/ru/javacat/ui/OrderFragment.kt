package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.common.utils.toBase64
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Point
import ru.javacat.ui.adapters.CustomersAdapter
import ru.javacat.ui.adapters.LocationAdapter
import ru.javacat.ui.adapters.OnCustomerListener
import ru.javacat.ui.adapters.OnLocationListener
import ru.javacat.ui.adapters.OnPointListener
import ru.javacat.ui.adapters.PointsAdapter
import ru.javacat.ui.databinding.FragmentOrderDetailsBinding
import ru.javacat.ui.utils.AndroidUtils
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.view_models.OrderViewModel

@AndroidEntryPoint
class OrderFragment:BaseFragment<FragmentOrderDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderDetailsBinding
        get() = {inflater, container ->
            FragmentOrderDetailsBinding.inflate(inflater, container, false)
        }

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var pointsAdapter: PointsAdapter
    private lateinit var customersAdapter: CustomersAdapter
    private lateinit var locationAdapter: LocationAdapter



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initAdapters()

        addEditTextListeners()

        documentsCheckBoxListener()

        viewModel.getLocations()

        viewModel.getCustomers()




        binding.addPointBtn.setOnClickListener {
            val place = binding.locationEditText.text.toString()
            val newLocation = Location(0, place)
            addPoint(newLocation)

            binding.locationEditText.text?.clear()
            viewModel.increaseDay()
        }

        binding.arrivalDate.setOnClickListener {
            parentFragmentManager.showCalendar {
                viewModel.setPointDate(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pointDate.collectLatest {
                    binding.arrivalDate.setText(it.asDayAndMonthFully())
                }
            }

        }

        lifecycleScope.launch {
            viewModel.locations.collectLatest {
                println("OrderFragment: $it")
                locationAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.points.collect{
                Log.i("MyTag","it: %$it")
                pointsAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.customers.collectLatest {
                Log.i("OrderFragment","it: %$it")
                customersAdapter.submitList(it)
            }
        }


        binding.plusDayBtn.setOnClickListener {
            viewModel.increaseDay()
        }

        binding.minusDayBtn.setOnClickListener {
            viewModel.decreaseDay()
        }



        binding.routeTextView.setOnClickListener {
            findNavController().navigate(R.id.action_newOrderFragment_to_addPointsFragment)
        }

        binding.newCustomerBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderFragment_to_newCustomerFragment)
        }
    }

    private fun initAdapters(){
        customersAdapter = CustomersAdapter(object : OnCustomerListener{
            override fun onCustomer(item: Customer) {
                binding.textInputEditText.setText(item.shortName)
                //binding.customersRecView.isGone = true
                AndroidUtils.hideKeyboard(requireView())

                //TODO добавить во вьюмодель Клиента
            }
        })
        binding.customersRecView.adapter = customersAdapter

        pointsAdapter = PointsAdapter(object : OnPointListener {
            override fun removePoint(item: Point) {
                viewModel.removePoint(item)
            }
        })
        binding.recyclerView.adapter = pointsAdapter

        locationAdapter = LocationAdapter(object : OnLocationListener{
            override fun onLocation(item: Location) {
                addPoint(item)
                viewModel.increaseDay()
                //binding.locationsRecView.isGone = true
                AndroidUtils.hideKeyboard(requireView())
            }
        })
        binding.locationsRecView.adapter = locationAdapter
    }

    private fun documentsCheckBoxListener(){
        binding.statusRadioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.pending -> {
                    binding.documentsGroup.isGone = true
                } else -> {
                binding.documentsGroup.isGone = false
                binding.scroll.scrollToDescendant(binding.documentsGroup)
                }
            }
        }
    }

    private fun addEditTextListeners(){

        binding.textInputEditText.setOnClickListener {
            //binding.customersRecView.isGone = false

        }

        binding.locationEditText.setOnClickListener {
            //binding.locationsRecView.isGone = false
            binding.scroll.scrollToDescendant(binding.addPointBtn)

        }

        binding.textInputEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //binding.customersRecView.isGone = false
                viewModel.searchCustomers(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.locationEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //binding.locationsRecView.isGone = false
                viewModel.searchLocations(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun addPoint(location: Location){
        //pointDate = binding.arrivalDate.text.toString().toLocalDate()
        val id = location.name.toBase64()
        val pointDate = viewModel.pointDate.value
        if (location.id == 0) {
            viewModel.insertNewLocation(location)
        }
        val newPoint = Point(id, location, pointDate)
        viewModel.addPoint(newPoint)
    }

}