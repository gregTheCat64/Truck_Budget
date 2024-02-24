package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.common.utils.toBase64
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Point
import ru.javacat.ui.adapters.CargoAdapter
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
    private lateinit var cargoAdapter: CargoAdapter

    private val itemParam = "item"
    private val bundle = Bundle()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val routeId = args?.getLong("route_id")?:0L

        binding.orderTitle.text = "Рейс №$routeId"

        initAdapters()

        addEditTextListeners()

        documentsCheckBoxListener()

        viewModel.getLocations()

        viewModel.getCustomers()

        viewModel.getCargos()

            //Добавляем Поинт
        binding.addPointBtn.setOnClickListener {
            val place = binding.locationEditText.text.toString()
            if (place.isNotEmpty()){
                //val id = place.toBase64()
                val newLocation = Location(0,place)
                viewModel.insertNewLocation(newLocation)

                addPoint(newLocation)
                binding.locationEditText.text?.clear()
                viewModel.increaseDay()
            }
        }

            //Добавляем Дату
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.cargo.collectLatest {
                    cargoAdapter.submitList(it)
                }
            }
        }


        binding.plusDayBtn.setOnClickListener {
            viewModel.increaseDay()
        }

        binding.minusDayBtn.setOnClickListener {
            viewModel.decreaseDay()
        }

        binding.customerInputEditText.setOnClickListener {
            addItemToRoute("CUSTOMER")
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
                binding.customerInputEditText.setText(item.shortName)
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

        cargoAdapter = CargoAdapter {
                AndroidUtils.hideKeyboard(requireView())
            }

        binding.cargoRecView.adapter = cargoAdapter
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

        binding.customerInputEditText.setOnClickListener {
            //binding.customersRecView.isGone = false

        }

        binding.locationEditText.setOnClickListener {
            //binding.locationsRecView.isGone = false
            binding.scroll.scrollToDescendant(binding.addPointBtn)

        }

        binding.customerInputEditText.addTextChangedListener(object : TextWatcher{
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

    private fun addItemToRoute(item: String){
        bundle.putString(itemParam, item)
        findNavController().navigate(R.id.chooseItemFragment, bundle)
    }

    private fun addPoint(location: Location){
        val pointDate = viewModel.pointDate.value
        val id = (location.name + pointDate.asDayAndMonthFully()).toBase64()
        val newPoint = Point(id, location, pointDate)
        viewModel.addPoint(newPoint)
    }

}