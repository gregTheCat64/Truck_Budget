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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.toBase64
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Point
import ru.javacat.ui.adapters.CustomersAdapter
import ru.javacat.ui.adapters.OnCustomerListener
import ru.javacat.ui.adapters.OnPointListener
import ru.javacat.ui.adapters.PointsAdapter
import ru.javacat.ui.databinding.FragmentOrderDetailsBinding
import ru.javacat.ui.view_models.OrderViewModel
import java.time.LocalDate

@AndroidEntryPoint
class OrderFragment:BaseFragment<FragmentOrderDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderDetailsBinding
        get() = {inflater, container ->
            FragmentOrderDetailsBinding.inflate(inflater, container, false)
        }

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var pointsAdapter: PointsAdapter
    private lateinit var customersAdapter: CustomersAdapter
    private lateinit var pointDate: LocalDate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pointDate = LocalDate.now()

        //var customers = emptyList<String>()

        //val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, customers)


        customersAdapter = CustomersAdapter(object : OnCustomerListener{
            override fun onCustomer(item: Customer) {
                binding.textInputEditText.setText(item.shortName)
                binding.customersRecView.isGone = true
                //TODO добавить во вьюмодель Клиента
            }
        })
        binding.customersRecView.adapter = customersAdapter

        binding.textInputEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.customersRecView.isGone = false
                viewModel.getCustomers(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        pointsAdapter = PointsAdapter(object : OnPointListener {
            override fun removePoint(item: Point) {
                viewModel.removePoint(item)
            }
        })
        binding.recyclerView.adapter = pointsAdapter

        lifecycleScope.launch {
            viewModel.points.collect{
                Log.i("MyTag","it: %$it")
                pointsAdapter.submitList(it)
            }
        }


        binding.plusDayBtn.setOnClickListener {
            increaseDay()
        }

        binding.minusDayBtn.setOnClickListener {
            decreaseDay()
        }

        binding.addPointBtn.setOnClickListener {
            val place = binding.pointEditText.text.toString()
            val id = place.toBase64()
            println("id of $place: $id")
            val newPoint = Point(id, Location(place), pointDate)
            viewModel.addPoint(newPoint)
            binding.pointEditText.text?.clear()
            increaseDay()
        }

        lifecycleScope.launch {
            viewModel.customers.collectLatest {
                Log.i("OrderFragment","it: %$it")
                customersAdapter.submitList(it)
            }
        }



        binding.routeTextView.setOnClickListener {
            findNavController().navigate(R.id.action_newOrderFragment_to_addPointsFragment)
        }

        binding.newCustomerBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderFragment_to_newCustomerFragment)
        }
    }

    private fun increaseDay(){
        pointDate = pointDate.plusDays(1)
        binding.dateTextView.text = pointDate.toString()
    }

    private fun decreaseDay(){
        pointDate = pointDate.minusDays(1)
        binding.dateTextView.text = pointDate.toString()
    }
}