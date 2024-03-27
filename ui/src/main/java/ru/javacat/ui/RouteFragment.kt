package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
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

//    private var prepay: Int? = null
//    private var routeSpending: Int? = null
//    private var routeDuration: Int? = null
//    private var fuelUsedUp: Int? = null
//    private var fuelPrice: Int? = null

    private val itemParam = "item"
    private val routeIdParam = "route_id"
    private var currentRoute = Route()
    private var isRouteLoaded = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args = arguments
        val routeId = args?.getLong(routeIdParam)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                when (it) {
                    LoadState.Loading -> binding.progressBar.isVisible = true
                    LoadState.Success.OK -> binding.progressBar.isGone = true
                    LoadState.Success.GoForward -> findNavController().navigate(R.id.orderDetailsFragment)
                    LoadState.Success.GoBack -> findNavController().navigate(R.id.routeListFragment)
                    else -> {}
                }
            }
        }

        if (!isRouteLoaded && routeId != null) {
            Log.i("routeFrag", "routeID: $routeId")
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getRouteAndUpdateEditedRoute(routeId)
                isRouteLoaded = true
            }
        }


        val adapter = OrdersAdapter{
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getOrderAndUpdateEditedOrder(it.id)
            }
        }
        binding.ordersList.adapter = adapter

        //инициализация UI
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedRoute.collectLatest {
                    currentRoute = it
                    binding.titleRoute.text =
                        "Рейс № ${it.id}"

                    binding.driverTv.text = it.driver?.surname
                    binding.truckTv.text = it.truck?.regNumber
                    binding.trailerTv.text = it.trailer?.regNumber
                    binding.prepayTv.text = it.prepayment?.toString()
                    binding.docsImg.isGone = it.orderList.isNotEmpty()
                    val subsistenceExp = it.routeDuration?.let { it1 -> it.payPerDiem?.times(it1).toString() + " руб" }
                    val fuelSpending = it.fuelUsedUp?.let { it1 -> it.fuelPrice?.times(it1).toString() + " руб" }
                    val routeSpending = it.routeSpending?.toString() + " руб"
                    val salary = it.driverSalary?.toString()+" руб"
                    val income = it.income?.toString()+" руб"
                    val netIncome = it.netIncome?.toString() + " руб"

                    if (it.isFinished){
                        binding.addOrderBtn.isGone = true
                        binding.finishRouteBtn.isGone = true
                        binding.editCountBtn.isGone = false
                        binding.finalCountFrame.isGone = false

                        binding.subsistenceExpensesTv.setText("${it.payPerDiem} руб. * ${it.routeDuration} = $subsistenceExp")
                        binding.fuelPriceTv.setText("${it.fuelPrice} руб. * ${it.fuelUsedUp} л. = $fuelSpending")
                        binding.otherSpendingTv.text = routeSpending
                        binding.salaryTv.text = salary
                        binding.incomeTv.text = income
                        binding.netIncomeTv.text = netIncome

                    } else  {
                        binding.finishRouteBtn.isGone = false
                        binding.finalCountFrame.isGone = true
                        binding.addOrderBtn.isGone = false
                    }


                    it.orderList.let {orders->
                        adapter.submitList(orders)
                    }
                }
            }
        }

        //Новый заказ
        binding.addOrderBtn.setOnClickListener {
            viewModel.clearEditedOrder()
            findNavController().navigate(R.id.addCustomerFragment)
        }

        //Возврат
        binding.closeBtn.setOnClickListener {
                //getFieldsData()
                //viewModel.saveRoute(true)
                findNavController().navigate(R.id.routeListFragment)
            }

        //Завершаем рейс
        binding.finishRouteBtn.setOnClickListener {
            calculate()
        }

        binding.editCountBtn.setOnClickListener{
            calculate()
        }


        super.onViewCreated(view, savedInstanceState)
    }


    private fun calculate(){
        if (currentRoute.orderList.isNotEmpty()){
            viewModel.setRouteFinished()
            findNavController().navigate(R.id.finishRouteFragment)
        } else {
            Toast.makeText(requireContext(), "Список заявок пуст!", Toast.LENGTH_SHORT).show()
        }
    }

}