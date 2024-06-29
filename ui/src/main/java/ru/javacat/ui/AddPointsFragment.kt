package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
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
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import ru.javacat.ui.adapters.LocationAdapter
import ru.javacat.ui.adapters.OnPointListener
import ru.javacat.ui.adapters.PointsAdapter
import ru.javacat.ui.databinding.FragmentAddPointsBinding
import ru.javacat.ui.utils.FragConstants.IS_NEW_ORDER
import ru.javacat.ui.utils.showCalendar
import ru.javacat.ui.view_models.AddPointsViewModel

@AndroidEntryPoint
class AddPointsFragment : BaseFragment<FragmentAddPointsBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddPointsBinding
        get() = { inflater, container ->
            FragmentAddPointsBinding.inflate(inflater, container, false)
        }

    private val viewModel: AddPointsViewModel by viewModels()

    private lateinit var pointsAdapter: PointsAdapter
    private lateinit var locationAdapter: LocationAdapter

    private var locationsFound: Boolean = false
    private var currentRoute:Route? = null

    private var isNewOrder = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        isNewOrder = args?.getBoolean(IS_NEW_ORDER)?: true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_cancel, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    R. id.cancel_button_menu_item-> {
                        if (isNewOrder){
                            findNavController().popBackStack(R.id.viewPagerFragment, false)
                        } else findNavController().popBackStack(R.id.orderDetailsFragment, false)
                        return true
                    }
                    else -> return false
                }
            }

        }, viewLifecycleOwner)

        initLocationAdapter()
        initPointAdapter()
        addEditTextListeners()


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                currentRoute = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedOrder.collectLatest {
                pointsAdapter.submitList(it?.points)
                it?.let {
                    viewModel.initPointList(it.points)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            if (currentRoute?.orderList?.isNotEmpty() == true){
                val lastDate = currentRoute?.orderList?.last()?.points?.last()?.arrivalDate
                lastDate?.plusDays(1)?.let { viewModel.setPointDate(it) }
            }
        }



        binding.okBtn.setOnClickListener {
            if (isNewOrder){
                findNavController().navigate(R.id.addPaymentFragment)
            } else findNavController().popBackStack(R.id.orderDetailsFragment, false)

        }

    }

    private fun initPointAdapter() {
        pointsAdapter = PointsAdapter(object : OnPointListener {
            override fun removePoint(item: Point) {
                viewModel.removePoint(item)
            }
        })
        binding.recyclerView.adapter = pointsAdapter


        //Добавляем Дату Поинта
        binding.dateTextView.setOnClickListener {
            parentFragmentManager.showCalendar {
                viewModel.setPointDate(it)
            }
        }

        //Добавляем Поинт
        binding.addPointBtn.setOnClickListener {
            val locationName = binding.locationEditText.text.toString()
            if (locationName.isNotEmpty()) {
                val newLocation = Location(null, locationName)
                viewModel.insertNewLocation(newLocation)
                addPoint(locationName)
                binding.locationEditText.text?.clear()
                binding.addPointBtn.isGone = true
                viewModel.increaseDay()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pointDate.collectLatest {
                    binding.dateTextView.setText(it.asDayAndMonthFully())
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
    }

    private fun initLocationAdapter() {
        viewModel.getLocations()
        locationAdapter = LocationAdapter {
            addPoint(it.nameToShow)
            viewModel.increaseDay()
            //binding.locationsRecView.isGone = true
            //AndroidUtils.hideKeyboard(requireView())
        }

        binding.locationsRecView.adapter = locationAdapter

        lifecycleScope.launch {
            viewModel.locations.collectLatest {
                println("OrderFragment: $it")
                locationAdapter.submitList(it)
                locationsFound = it?.size != 0

            }
        }
    }

    private fun addPoint(locationName: String) {
        val pointDate = viewModel.pointDate.value
        val pointSize = viewModel.pointList.size
        //val ordersSize = viewModel.orderList.size

        //val id = ("Rt"+currentRoute.id.toString()+"Ord"+(ordersSize+1).toString()+"pt"+(pointSize+1).toString())
        val newPoint = Point("", locationName, pointDate)
        viewModel.addPoint(newPoint)
    }

    private fun addEditTextListeners() {
        binding.locationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchLocations(p0.toString())
                if (!locationsFound){
                    binding.addPointBtn.isVisible = true
                    binding.addPointBtn.text = "Сохранить $p0"
                } else  binding.addPointBtn.isVisible = false
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!locationsFound){
                    binding.addPointBtn.isVisible = true
                    binding.addPointBtn.text = "Сохранить $p0"
                } else  binding.addPointBtn.isVisible = false
            }
        })

    }
}