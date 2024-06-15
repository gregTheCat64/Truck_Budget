package ru.javacat.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.databinding.FragmentFinishPartnerRouteBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.FinishPartnerRouteViewModel

@AndroidEntryPoint
class FinishPartnerRouteFragment: BottomSheetDialogFragment() {

    private val viewModel: FinishPartnerRouteViewModel by viewModels()
    private lateinit var binding: FragmentFinishPartnerRouteBinding

    var revenue = 0
    var contractorsCost = 0
    var profit = 0
    var moneyToPay = 0
    var prepayment = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinishPartnerRouteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("FinishPartnerFrag", "onViewCreated")
        val args = arguments
        val routeId = args?.getLong(FragConstants.ROUTE_ID)
        Log.i("FinishPartnerFrag", "routeId: $routeId")

        viewLifecycleOwner.lifecycleScope.launch {
            if (routeId != null){
                viewModel.getEditedRoute(routeId)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editedRoute.collectLatest {
                initUi(it)
            }
        }

        binding.saveBtn.setOnClickListener {
            if (!binding.profitEt.text.isNullOrEmpty()){
                viewModel.saveRoute(
                    binding.profitEt.text.toString().toFloat(),
                    revenue,
                    moneyToPay,
                    prepayment
                    )
            } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadState.collectLatest {
                when (it) {
                    LoadState.Success.GoBack -> this@FinishPartnerRouteFragment.dismiss()
                   else ->Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
                       .show()
                }
            }
        }
    }


    private fun initUi(route: Route) {
        prepayment = route.countRoute?.prepayment?:0

        for (i in route.orderList){
            revenue = revenue + i.price
            contractorsCost = contractorsCost + i.contractorPrice!!
        }
        profit =  revenue - contractorsCost
        moneyToPay = contractorsCost-prepayment
        binding.revenueTv.setText("$revenue ${getString(R.string.rub)}")
        binding.contractorCostTv.setText("$contractorsCost ${getString(R.string.rub)}")
        binding.profitEt.setText("$profit")
    }
}