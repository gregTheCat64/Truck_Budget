package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.ui.databinding.FragmentMonthsDialogBinding
import ru.javacat.ui.utils.FragConstants
import java.time.Month



@AndroidEntryPoint
class MonthsDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMonthsDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonthsDialogBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val monthNames = arrayOf(
            getString(R.string.any),
            getString(R.string.january),
            getString(R.string.february),
            getString(R.string.march),
            getString(R.string.april),
            getString(R.string.may),
            getString(R.string.june),
            getString(R.string.july),
            getString(R.string.august),
            getString(R.string.september),
            getString(R.string.october),
            getString(R.string.november),
            getString(R.string.december))


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, monthNames)
        binding.monthsList.adapter = adapter

        binding.monthsList
            .onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            val bundle = Bundle()
            Log.i("MonthsDialog", "pos: $pos")
            bundle.putInt(FragConstants.FILTER_MONTH, pos)
            setFragmentResult(FragConstants.FILTER_ORDER, bundle)
            this.dismiss()
        }
    }
}