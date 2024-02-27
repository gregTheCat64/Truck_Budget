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
import androidx.lifecycle.LifecycleOwner
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
import ru.javacat.domain.models.Route
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
class OrderFragment : BaseFragment<FragmentOrderDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderDetailsBinding
        get() = { inflater, container ->
            FragmentOrderDetailsBinding.inflate(inflater, container, false)
        }

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var pointsAdapter: PointsAdapter
    private lateinit var customersAdapter: CustomersAdapter
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var cargoAdapter: CargoAdapter

    private var currentRoute = Route()

    private val itemParam = "item"
    private val bundle = Bundle()
    private var routeId = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("OrderFrag", "editedRoute: ${viewModel.editedRoute.value}")

        //Подписка на редактируемый Рейс
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                Log.i("OrderFrag", "editedRoute: ${it}")
                binding.orderTitle.text = "Рейс №${it.id}"
                currentRoute = it
                routeId = it.id?:0L
            }
        }

        initCargoAdapter()
        initLocationAdapter()
        initPointAdapter()
        initCustomerAdapter()

        viewModel.getLocations()
        viewModel.getCustomers()
        viewModel.getCargos()
        viewModel.setOrderList()


        //инициализация UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest {
                    binding.customerInputEditText.setText(it.customer?.name)
                    pointsAdapter.submitList(it.points)
                }
            }
        }

        addEditTextListeners()

        documentsCheckBoxListener()

        //Добавляем Дату Поинта
        binding.arrivalDate.setOnClickListener {
            parentFragmentManager.showCalendar {
                viewModel.setPointDate(it)
            }
        }

        //Добавляем Поинт
        binding.addPointBtn.setOnClickListener {
            val place = binding.locationEditText.text.toString()
            if (place.isNotEmpty()) {
                val newLocation = Location(null, place)
                viewModel.insertNewLocation(newLocation)
                addPoint(newLocation)
                binding.locationEditText.text?.clear()
                viewModel.increaseDay()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pointDate.collectLatest {
                    binding.arrivalDate.setText(it.asDayAndMonthFully())
                }
            }
        }

        //Плюс день
        binding.plusDayBtn.setOnClickListener {
            viewModel.increaseDay()
        }

        //Минус день
        binding.minusDayBtn.setOnClickListener {
            viewModel.decreaseDay()
        }


        //Новый клиент
        binding.newCustomerBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderFragment_to_newCustomerFragment)
        }

        binding.saveBtn.setOnClickListener {
            //TODO подумать над айдишниками!
            val id = "Rt$routeId"+"Ord"+(currentRoute.orderList.size+1).toString()
            val newOrder = viewModel.editedOrder.value.copy(
                id,
                routeId= routeId,
                driverId = currentRoute.driver?.id?:0,
                truckId = currentRoute.truck?.id?:0)
            viewModel.saveOrder(newOrder)
            //viewModel.updateEditedRoute(newOrder)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it == LoadState.Success.GoBack){
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun initCustomerAdapter() {
        customersAdapter = CustomersAdapter(object : OnCustomerListener {
            override fun onCustomer(item: Customer) {
                binding.customerInputEditText.setText(item.shortName)
                //binding.customersRecView.isGone = true
                viewModel.setCustomer(item)
                AndroidUtils.hideKeyboard(requireView())
            }
        })
        binding.customersRecView.adapter = customersAdapter

        lifecycleScope.launch {
            viewModel.customers.collectLatest {
                Log.i("OrderFragment", "it: %$it")
                customersAdapter.submitList(it)
            }
        }
    }

    private fun initPointAdapter() {
        pointsAdapter = PointsAdapter(object : OnPointListener {
            override fun removePoint(item: Point) {
                viewModel.removePoint(item)
            }
        })
        binding.recyclerView.adapter = pointsAdapter

    }

    private fun initLocationAdapter() {
        locationAdapter = LocationAdapter(object : OnLocationListener {
            override fun onLocation(item: Location) {
                addPoint(item)
                viewModel.increaseDay()
                //binding.locationsRecView.isGone = true
                AndroidUtils.hideKeyboard(requireView())
            }
        })
        binding.locationsRecView.adapter = locationAdapter

        lifecycleScope.launch {
            viewModel.locations.collectLatest {
                println("OrderFragment: $it")
                locationAdapter.submitList(it)
            }
        }
    }

    private fun initCargoAdapter() {
        cargoAdapter = CargoAdapter {
            AndroidUtils.hideKeyboard(requireView())
        }

        binding.cargoRecView.adapter = cargoAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cargo.collectLatest {
                    cargoAdapter.submitList(it)
                }
            }
        }
    }

    private fun documentsCheckBoxListener() {
        binding.statusRadioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.pending -> {
                    binding.documentsGroup.isGone = true
                }

                else -> {
                    binding.documentsGroup.isGone = false
                    binding.scroll.scrollToDescendant(binding.documentsGroup)
                }
            }
        }
    }

    private fun addEditTextListeners() {

        binding.customerInputEditText.setOnClickListener {
            addItemToRoute("CUSTOMER")
        }

        binding.locationEditText.setOnClickListener {
            //binding.locationsRecView.isGone = false
            binding.scroll.scrollToDescendant(binding.addPointBtn)

        }

        binding.customerInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //binding.customersRecView.isGone = false
                viewModel.searchCustomers(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.locationEditText.addTextChangedListener(object : TextWatcher {
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

    private fun addItemToRoute(item: String) {
        bundle.putString(itemParam, item)
        findNavController().navigate(R.id.chooseItemFragment, bundle)
    }

    private fun addPoint(location: Location) {

        val pointDate = viewModel.pointDate.value
        val pointSize = viewModel.pointList.size
        val ordersSize = viewModel.orderList.size

        val id = ("Rt"+routeId.toString()+"Ord"+(ordersSize+1).toString()+"pt"+(pointSize+1).toString() )
        val newPoint = Point(id, location, pointDate)
        viewModel.addPoint(newPoint)
    }

}