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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)
//
//        requireActivity().addMenuProvider(object : MenuProvider{
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_empty, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//               when (menuItem.itemId) {
//                   android.R.id.home -> {
//                       findNavController().navigateUp()
//                       return true
//                   }
//                   else -> return false
//               }
//            }
//        }, viewLifecycleOwner)
        binding = FragmentEditPointsBinding.inflate(layoutInflater)

        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        addEditTextListeners()

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
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pointDate.collectLatest {
                    binding.dateTextView.setText(it.asDayAndMonthFully())
                }
            }
        }

        //ДАТА:
        viewLifecycleOwner.lifecycleScope.launch {
            //если городов в маршруте пока нет, мы берем дату последней заявки, плюс один день
            //если города в заявке есть, то берем дату последнего, плюс один день
            if (currentOrder?.points?.isEmpty() == true){
                if (currentRoute?.orderList?.isNotEmpty() == true){
                    val lastDate = currentRoute?.orderList?.last()?.points?.last()?.arrivalDate
                    //val lastDate =
                    lastDate?.plusDays(1)?.let { viewModel.setPointDate(it) }
                }
            } else {
                val lastDate = currentOrder?.points?.last()?.arrivalDate
                lastDate?.plusDays(1)?.let { viewModel.setPointDate(it) }
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
                addPoint(locationName)
            } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initUi(){
        viewModel.getLocations()
        locationAdapter = LocationAdapter(object : OnModelWithRemoveBtnListener{
            override fun onItem(model: BaseNameModel<Long>) {
                addPoint(model.nameToShow)
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

    private fun addPoint(locationName: String) {
        val pointDate = viewModel.pointDate.value

        val newPoint = Point("", locationName, pointDate)
        viewModel.addPoint(newPoint)
    }

    private fun addEditTextListeners() {
        binding.locationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchLocations(p0.toString())
                binding.okBtn.isVisible = !p0.isNullOrEmpty()
                binding.okBtn.text = "Cоздать место: $p0"
//                if (!locationsFound){
//                    binding.addPointBtn.isVisible = true
//                    binding.addPointBtn.text = "Сохранить $p0"
//                } else  binding.addPointBtn.isVisible = false
            }

            override fun afterTextChanged(p0: Editable?) {
//                if (!locationsFound){
//                    binding.addPointBtn.isVisible = true
//                    binding.addPointBtn.text = "Сохранить $p0"
//                } else  binding.addPointBtn.isVisible = false
            }
        })

    }
}