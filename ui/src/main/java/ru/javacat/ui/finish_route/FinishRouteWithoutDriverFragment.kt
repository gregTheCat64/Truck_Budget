package ru.javacat.ui.finish_route

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.SalaryCountMethod
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentFinishRouteWithoutDriverBinding
import ru.javacat.ui.utils.FragConstants
import java.time.LocalDate
import java.time.Period

@AndroidEntryPoint
class FinishRouteWithoutDriverFragment: BaseFragment<FragmentFinishRouteWithoutDriverBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentFinishRouteWithoutDriverBinding
        get() = {inflater, container ->
            FragmentFinishRouteWithoutDriverBinding.inflate(inflater, container, false)
        }

    private val viewModel: FinishRouteWithoutDriverViewModel by viewModels()

    private var currentRoute: Route? = null

    private var firstDate: LocalDate? = null
    private var lastDate: LocalDate? = null
    private var routeDuration: Int? = null

    private var revenue: Int? = null

    private var fuelUsedUp: Int? = null
    private var fuelPrice: Float? = null

    private var extraExpenses: Int? = null
    private var roadFee: Int? = null

    private var totalExpenses: Float? = null
    private var profit: Float? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_save, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    R.id.save -> {
                        saveRoute()
                        return true
                    }
                    else -> return false
                }
            }
        }, viewLifecycleOwner)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val routeId = args?.getLong(FragConstants.ROUTE_ID)
        Log.i("FinishRouteWoDriverFrag", "routeId: $routeId")

        //Получаем текущий рейс
        viewLifecycleOwner.lifecycleScope.launch {
            if (routeId != null) viewModel.getEditedRoute(routeId)
        }

        //Если рейс закончен - редакция, если нет, то открываем новый
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                if (it != null) {
                    currentRoute = it
                    revenue = viewModel.calculateRevenue(it)

                    if (!it.isFinished) {
                        getDataFromLastRoute()
                    } else getDataFromCurrentRoute(it)
                }
            }
        }

        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it is LoadState.Success.GoBack) findNavController().navigateUp()
            }
        }

        //считаем прибыль
        binding.calculateRouteBtn.setOnClickListener {
            if (getFields()) {
                totalExpenses = viewModel.calculateTotalExpenses(
                    0f, fuelUsedUp!!, fuelPrice!!, 0, 0 , 0,
                    0, extraExpenses!!, 0, roadFee!!
                    )
                profit = viewModel.calculateProfit(revenue!!, totalExpenses!!)
                routeDuration = currentRoute?.let { route -> calculateRouteDuration(route) }

                binding.revenueTv.setText(revenue.toString())
                binding.profitTv.setText(profit.toString())
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fill_requested_fields),
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.saveBtn.setOnClickListener {
            saveRoute()
        }
    }

    private fun getDataFromLastRoute(){
        println("getDataFromLastRoute")
        viewModel.getLastRoute()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.lastRoute.collectLatest {
                val lastRoute = viewModel.lastRoute.value
                fuelPrice = lastRoute.routeDetails?.fuelPrice
            }
        }
        updateUi()
    }

    private fun getDataFromCurrentRoute(route: Route){
        route.let {
            extraExpenses = it.routeDetails?.extraExpenses
            roadFee = it.routeDetails?.roadFee
            fuelPrice = it.routeDetails?.fuelPrice
            fuelUsedUp = it.routeDetails?.fuelUsedUp
        }
        updateUi()
    }

    private fun updateUi(){
        extraExpenses?.let {
            binding.otherExpenses.setText(it.toString())
        }
        roadFee?.let {
            binding.roadFee.setText(it.toString())
        }
        fuelUsedUp?.let {
            binding.fuelUsedUp.setText(it.toString())
        }
        fuelPrice?.let {
            binding.fuelPrice.setText(it.toString())
        }
    }

    private fun getFields(): Boolean{
        extraExpenses = if (!binding.otherExpenses.text.isNullOrEmpty()){
            binding.otherExpenses.text.toString().toInt()
        } else 0

        roadFee = if (!binding.roadFee.text.isNullOrEmpty()){
            binding.roadFee.text.toString().toInt()
        } else 0

        if (!binding.fuelUsedUp.text.isNullOrEmpty()) {
            fuelUsedUp = binding.fuelUsedUp.text.toString().toInt()
        } else return false

        if (!binding.fuelPrice.text.isNullOrEmpty()) {
            fuelPrice = binding.fuelPrice.text.toString().toFloat()
        } else return false

        return true
    }

    private fun calculateRouteDuration(route: Route): Int {
        firstDate = route.orderList.firstOrNull()?.points?.first()?.arrivalDate
        lastDate = route.orderList.lastOrNull()?.points?.last()?.arrivalDate

        val period = Period.between(firstDate, lastDate)
        return period.days
    }

    private fun saveRoute(){
        if (getFields()) {
            viewModel.saveRoute(
                0,
                0,
                extraExpenses?:0,
                roadFee?:0,
                routeDuration?:0,
                0,
                fuelUsedUp!!,
                fuelPrice!!,
                revenue!!,
                profit!!,
                null,
                totalExpenses?:0f
            )
        } else Toast.makeText(
            requireContext(),
            "Заполните все поля и нажмите кнопку принять",
            Toast.LENGTH_SHORT
        ).show()
    }
}