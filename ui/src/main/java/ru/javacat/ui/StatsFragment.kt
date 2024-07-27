package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.asShortMonth
import ru.javacat.domain.models.MonthlyProfit
import ru.javacat.domain.models.StatsModel
import ru.javacat.ui.adapters.MonthlyProfitAdapter
import ru.javacat.ui.databinding.FragmentStatsBinding

import ru.javacat.ui.view_models.StatsViewModel
import java.time.Month

@AndroidEntryPoint
class StatsFragment: BaseFragment<FragmentStatsBinding>() {
    private val viewModel: StatsViewModel by viewModels()

    private lateinit var monthlyProfitAdapter: MonthlyProfitAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentStatsBinding
        get() = {inflater, container ->
            FragmentStatsBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()


        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)


        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_edit_remove, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }

                    R.id.edit_menu_item -> {

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




        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    when {
                        it is LoadState.Loading -> {
                            binding.progressBar.isGone = false
                        }
                        it is LoadState.Success.OK -> {
                            binding.progressBar.isGone = true
                        }
                    }
                }
            }
        }


//        val monthlyProfitAdapter = MonthlyProfitAdapter()
//        binding.profitList.adapter = monthlyProfitAdapter

        viewModel.getMonthlyIncomeByYear("2024")
        viewModel.getCompanyOrdersCountByYear("2024")
        viewModel.getNotCompanyOrdersCountByYear("2024")
        viewModel.getCompanyRoutesCountByYear("2024")

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.monthlyProfitList.collectLatest {
                   println(it.toString())
                    it?.let { buildChart(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stats.collectLatest {
                    it?.let { updateUi(it) }
                }
            }
        }
    }

    private fun buildChart(list: List<MonthlyProfit>){
        Log.i("StatsFrag", "building chart")
        val chart = binding.chart

        val profits = ArrayList<BarEntry>()

        val monthNames = listOf(Month.JANUARY.asShortMonth(), Month.FEBRUARY.asShortMonth(), Month.MARCH.asShortMonth(), Month.APRIL.asShortMonth(),
            Month.MAY.asShortMonth(), Month.JUNE.asShortMonth(), Month.JULY.asShortMonth(), Month.AUGUST.asShortMonth(), Month.SEPTEMBER.asShortMonth(), Month.OCTOBER.asShortMonth(),
            Month.NOVEMBER.asShortMonth(), Month.DECEMBER.asShortMonth())

        Log.i("StatsFrag", "profits: $profits")

        for (m in monthNames){
            profits.add(BarEntry(0F, 0F))
        }

        for (l in list){
            profits.add(BarEntry((l.month!!.value-1).toFloat(), l.totalProfit.toFloat()))
        }


        val dataSet = BarDataSet(profits, "Ежемесячный доход")
        dataSet.color = R.color.md_theme_primary

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(monthNames)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        chart.data = BarData(dataSet)
        chart.setFitBars(true)
        chart.description.text = ""
        chart.animateY(1000)
        chart.invalidate()
    }

    private fun updateUi(stats: StatsModel){
        binding.companyRoutesCount.setText(stats.companyRoutesCount.toString())
        binding.companyOrdersCount.setText(stats.companyOrdersCount.toString())
        binding.notCompanyOrdersCount.setText(stats.notCompanyOrdersCount.toString())
        binding.totalYearProfit.setText(stats.totalProfit.toString())
        binding.averageProfit.setText(stats.companyAverageMonthlyProfit.toString())
    }

}