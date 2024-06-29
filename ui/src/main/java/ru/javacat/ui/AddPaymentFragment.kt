package ru.javacat.ui

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
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.ui.databinding.FragmentAddPaymentBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.FragConstants.IS_NEW_ORDER
import ru.javacat.ui.view_models.AddPaymentViewModel

@AndroidEntryPoint
class AddPaymentFragment : BaseFragment<FragmentAddPaymentBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddPaymentBinding
        get() = { inflater, container ->
            FragmentAddPaymentBinding.inflate(inflater, container, false)
        }

    private val viewModel: AddPaymentViewModel by viewModels()

    private var isNewOrder = true

    private var currentOrder: Order? = null
    private var currentRoute: Route? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        isNewOrder = args?.getBoolean(IS_NEW_ORDER) ?: true
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    currentOrder = order
                    viewModel.getRouteById(order.routeId)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                currentRoute = it
                initUi(currentOrder)
            }
        }

        binding.paymentDeadlineChipGroup.setOnCheckedStateChangeListener { chipGroup, list ->
            val checkedId = chipGroup.checkedChipId
            //Log.i("AddpaymentFrag", "checkedId: $checkedId")
            val chip = chipGroup.findViewById<Chip>(checkedId)
            binding.daysToPayEditText.setText(chip?.tag?.toString())
        }


        binding.okBtn.setOnClickListener {
           addPaymentToOrder()
        }

        //navigation


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it == LoadState.Success.GoForward) {
                    findNavController().navigate(R.id.orderDetailsFragment)
                }
            }
        }
    }

    private fun initUi(order: Order?){
        Log.i("AddPaymFrag", "contractorId: ${currentRoute?.contractor?.company?.id}")
        binding.contractorsFrame.isVisible = currentRoute?.contractor?.company?.id != FragConstants.MY_COMPANY_ID

        order?.price?.let {
            if (it != 0) binding.price.setText(it.toString())
        }
        order?.daysToPay?.let {
            if (it!= 0) binding.daysToPayEditText.setText(it.toString())
        }
        order?.contractorPrice?.let {
            if (it != 0) binding.contractorsPrice.setText(it.toString())
        }
    }

    private fun addPaymentToOrder(){
        val daysToPay = binding.daysToPayEditText.text?.let {
            if (it.isEmpty()) null else {
                it.toString().toInt()
            }
        }

        val price = binding.price.text.let {
            if (it.isNullOrEmpty()) {
                null
            } else it.toString().toInt()
        }

        val contractorPrice = binding.contractorsPrice.text.let {
            if (it.isNullOrEmpty()) {
                null
            } else it.toString().toInt()
        }

        if (price != null) {
            viewModel.addPaymentToOrder(price, daysToPay, contractorPrice)
        } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()

    }
}