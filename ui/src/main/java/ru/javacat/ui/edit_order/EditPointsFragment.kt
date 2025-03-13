package ru.javacat.ui.edit_order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.domain.models.BaseNameModel
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.adapters.LocationAdapter
import ru.javacat.ui.adapters.my_adapter.OnModelWithRemoveBtnListener
import ru.javacat.ui.databinding.FragmentEditPointsBinding
import ru.javacat.ui.utils.showCalendar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class EditPointsFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEditPointsBinding
//    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentEditPointsBinding
//        get() = {inflater, container ->
//            FragmentEditPointsBinding.inflate(inflater, container, false)
//        }

    private val viewModel: EditPointsViewModel by viewModels()
    private lateinit var locationAdapter: LocationAdapter

    private var currentRoute: Route? = null
    private var currentOrder: Order? = null
    private var locationsFound: Boolean = false

    private var locationName: String? = null
    private var arrivalDate: LocalDate? = null
    private var position: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let { bundle ->
            position = bundle.getInt("position")
            locationName = bundle.getString("location")
            val dateString = bundle.getString("date")
            arrivalDate = dateString?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
        }

        binding = FragmentEditPointsBinding.inflate(layoutInflater)

        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        addEditTextListeners()

        //навигация
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    when {
                        it is LoadState.Success.GoBack -> dismiss()
                    //findNavController().navigateUp()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                it?.let{currentRoute = it}
                Log.i("EditPointsFrag", "currentRouteId: ${it?.id}")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedOrder.collectLatest {
                it?.let { currentOrder = it }
                if (arrivalDate == null) {
                    calculateDate()
                    println("я тут тут тут")
                    //binding.okBtn.isGone = false
                }
            }
        }

            //Работа с датой:
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pointDate.collectLatest {
                    println("получили, обновляем дату в форме $it")
                    binding.dateTextView.setText(it.asDayAndMonthFully())
                }
            }
        }

        binding.minusDayBtn.setOnClickListener {
            viewModel.decreaseDay()
        }

        binding.plusDayBtn.setOnClickListener {
            viewModel.increaseDay()
        }

        binding.dateTextView.setOnClickListener {
            parentFragmentManager.showCalendar {
                viewModel.setPointDate(it)
            }
        }

        binding.okBtn.setOnClickListener {
            val locationName = binding.locationEditText.text.toString().trim()
            if (!locationsFound && locationName.isNotEmpty()) {
                val newLocation = Location(null, locationName)
                viewModel.insertNewLocation(newLocation)
                addPoint(locationName, position)
            } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initUi(){

        viewModel.getLocations()

        if (locationName != null && arrivalDate != null){
            binding.locationEditText.setText(locationName)
            viewModel.setPointDate(arrivalDate!!)
        }

        locationAdapter = LocationAdapter(object : OnModelWithRemoveBtnListener{
            override fun onItem(model: BaseNameModel<Long>) {
                addPoint(model.nameToShow, position)
            }

            override fun onRemove(model: BaseNameModel<Long>) {
                model.id?.let { viewModel.removeLocation(it) }

            }
        })

        binding.locationsRecView.adapter = locationAdapter

        val locationsLayoutManager = FlexboxLayoutManager(requireContext())
        locationsLayoutManager.flexDirection = FlexDirection.ROW
        locationsLayoutManager.justifyContent = JustifyContent.FLEX_START
        binding.locationsRecView.layoutManager = locationsLayoutManager

        lifecycleScope.launch {
            viewModel.locations.collectLatest {
                println("OrderFragment: $it")
                locationAdapter.submitList(it)
                locationsFound = it?.size != 0
            }
        }
    }

    private fun addPoint(locationName: String, position: Int) {
        //val pointDate = viewModel.pointDate.value
        //val newPoint = Point(0, locationName, pointDate)
        viewModel.addPoint(locationName, position)
    }

    //если городов в маршруте пока нет, мы берем дату последней заявки, плюс один день
    //если города в заявке есть, то берем дату последнего, плюс один день
    private fun calculateDate(){
        println("считаем дату")
        println("currentorder points: ${currentOrder?.points}")
        if (currentOrder?.points?.isEmpty() == true){
            if (currentRoute?.orderList?.isNotEmpty() == true){
                val lastDate = currentRoute?.orderList?.last()?.points?.last()?.arrivalDate
                lastDate?.plusDays(1)?.let {
                    println("дата равна $it")
                    viewModel.setPointDate(it)
                }

            }
        } else {
            val lastDate = currentOrder?.points?.last()?.arrivalDate
            lastDate?.plusDays(1)?.let {
                println("дата равна $it")
                viewModel.setPointDate(it) }
        }
    }

    private fun addEditTextListeners() {
        binding.locationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchLocations(p0.toString())
                binding.okBtn.isVisible = !p0.isNullOrEmpty()
                binding.okBtn.text = "Cоздать точку: $p0"
//                if (!locationsFound){
//                    binding.addPointBtn.isVisible = true
//                    binding.addPointBtn.text = "Сохранить $p0"
//                } else  binding.addPointBtn.isVisible = false
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}