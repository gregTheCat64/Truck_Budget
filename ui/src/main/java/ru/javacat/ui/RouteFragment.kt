package ru.javacat.ui

import android.os.Bundle
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
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.OrdersAdapter
import ru.javacat.ui.databinding.FragmentRouteBinding
import ru.javacat.ui.view_models.RouteViewModel

@AndroidEntryPoint
class RouteFragment : BaseFragment<FragmentRouteBinding>() {

    private val viewModel: RouteViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteBinding
        get() = { inflater, container ->
            FragmentRouteBinding.inflate(inflater, container, false)
        }

    private var prepay: Int? = null
    private var routeSpending: Int? = null
    private var routeDuration: Int? = null
    private var fuelUsedUp: Int? = null
    private var fuelPrice: Int? = null

    private val itemParam = "item"
    private val routeIdParam = "route_id"
    private val bundle = Bundle()
    private var currentId = 0L


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //инициализация UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest {
                    binding.titleRoute.text =
                        "Рейс № ${it.id}"

                    binding.addDriverEditText.setText(it.driver?.fullName)
                    binding.addTruckEditText.setText(it.truck?.regNumber)
                    binding.addTrailerEditText.setText(it.trailer?.regNumber)
                    binding.prepayEditText.setText(it.prepayment?.toString())
                    binding.routeDaysCount.setText(it.routeDuration?.toString())
                    binding.routeSpending.setText(it.routeSpending?.toString())
                    binding.fuelPrice.setText(it.fuelPrice?.toString())
                    binding.fuelUsedUp.setText(it.fuelUsedUp?.toString())
                }
            }
        }

        //Адаптер заказов:
        val adapter = OrdersAdapter()
        binding.ordersList.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
           viewModel.editedRoute.collectLatest {
               it.orderList.let {orders->
                    adapter.submitList(orders)
               }
           }
        }


        //Новый заказ
        binding.addOrderBtn.setOnClickListener {
            getFieldsData()
            viewModel.saveRoute(false)
            //val bundle = Bundle()
            //bundle.putLong(routeIdParam, viewModel.editedRoute.value.id?:0)
            //findNavController().navigate(R.id.action_routeFragment_to_newOrderFragment)
        }

        //Добавляем водителя
        binding.addDriverEditText.setOnClickListener {
            addItemToRoute("DRIVER")
        }

        //Добавляем тягач
        binding.addTruckEditText.setOnClickListener {
            addItemToRoute("TRUCK")
        }

        //Добавляем прицеп
        binding.addTrailerEditText.setOnClickListener {
           addItemToRoute("TRAILER")
        }

        //Сохраняем рейс
        binding.saveBtn.setOnClickListener {
                getFieldsData()
                viewModel.saveRoute(true)
                //findNavController().navigateUp()
            }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                when (it) {
                    LoadState.Success.GoForward -> findNavController().navigate(R.id.orderDetailsFragment)
                    LoadState.Success.GoBack -> findNavController().navigateUp()
                    else -> {}
                }


            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun addItemToRoute(item: String){
        getFieldsData()
        updateEditedRoute()

        bundle.putString(itemParam, item)
        findNavController().navigate(R.id.chooseItemFragment, bundle)
    }


    private fun updateEditedRoute() {
        viewModel.updateRoute()
    }

    private fun getFieldsData() {
        prepay = binding.prepayEditText.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        }

        routeSpending = binding.routeSpending.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        }

        routeDuration = binding.routeDaysCount.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        }

        fuelUsedUp = binding.fuelUsedUp.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        }

        fuelPrice = binding.fuelPrice.text?.let {
            if (it.isBlank()) null else it.toString().toInt()
        }

        viewModel.setFieldsData(prepay, routeSpending, routeDuration,fuelUsedUp,fuelPrice)
    }
}