package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.ui.databinding.FragmentAddPaymentBinding
import ru.javacat.ui.utils.FragConstants
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        isNewOrder = args?.getBoolean(IS_NEW_ORDER) ?: true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity). supportActionBar?.title = "Оплата"

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    if (order != null) {
                        initUi(order)
                    }
                }
            }
        }


        binding.okBtn.setOnClickListener {
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

            if (price != null) {
                viewModel.addPaymentToOrder(price, daysToPay)
            } else Toast.makeText(requireContext(), "Заполните ставку!", Toast.LENGTH_SHORT).show()
        }

        binding.cancelBtn.setOnClickListener {
            if (isNewOrder){
                findNavController().popBackStack(R.id.viewPagerFragment, false)
            } else findNavController().popBackStack(R.id.orderDetailsFragment, false)


        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                if (it == LoadState.Success.GoForward) {
                    //val bundle = Bundle()
                    //bundle.putBoolean(FragConstants.CREATING_ORDER, true)
                    findNavController().navigate(R.id.orderDetailsFragment)
                }
            }
        }
    }

    private fun initUi(order: Order){
        order.price.let {
            if (it != 0) binding.price.setText(it.toString())
        }
        order.daysToPay?.let {
            if (it!= 0) binding.daysToPayEditText.setText(it.toString())
        }
    }
}