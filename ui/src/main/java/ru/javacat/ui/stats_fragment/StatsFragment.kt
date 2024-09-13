package ru.javacat.ui.stats_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import ru.javacat.domain.models.YearHolder
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentStatsBinding
import ru.javacat.ui.utils.showYearCalendar

import java.time.Month

@AndroidEntryPoint
class StatsFragment: BaseFragment<FragmentStatsBinding>() {
    private val viewModel: StatsViewModel by viewModels()

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

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseYearBtn.text = YearHolder.selectedYear.toString()

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

        updateList()


        binding.chooseYearBtn.setOnClickListener {
            showYearCalendar {
                    selectedYear ->
                YearHolder.selectedYear = selectedYear
                binding.chooseYearBtn.text = selectedYear.toString()
                updateList()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.stats.collectLatest {
                    updateUi(it)
                }
            }
        }
    }

    private fun updateList(){
        viewModel.updateStats(year = YearHolder.selectedYear.toString())
    }

    private fun updateUi(stats: StatsModel){
        binding.companyRoutesCount.setText(stats.companyRoutesCount.toString())
        binding.companyOrdersCount.setText(stats.companyOrdersCount.toString())
        binding.notCompanyOrdersCount.setText(stats.notCompanyOrdersCount.toString())
        binding.totalYearProfit.setText(stats.totalProfit.toString())
        binding.averageProfit.setText(stats.companyAverageMonthlyProfit.toString())
        binding.notCompanyTransportAverageProfit.setText(stats.notCompanyAverageMonthlyProfit.toString())
        binding.notCompanyTotalProfit.setText(stats.notCompanyTotalProfit.toString())
        binding.yearExpense.setText(stats.totalExpense.toString())

        //stats.monthlyProfitList?.let { buildChart(it) }
        if (stats.monthlyProfitList!= null && stats.monthlyExpenseList != null){
            buildChart(stats.monthlyProfitList!!, stats.monthlyExpenseList!!)
        }

    }

    private fun buildChart(profitList: List<MonthlyProfit>, expenseList: List<MonthlyProfit>){
        Log.i("StatsFrag", "building chart")
        val chart = binding.chart

        val profits = ArrayList<BarEntry>()
        val expenses = ArrayList<BarEntry>()

        val monthNames = listOf(Month.JANUARY.asShortMonth(), Month.FEBRUARY.asShortMonth(), Month.MARCH.asShortMonth(), Month.APRIL.asShortMonth(),
            Month.MAY.asShortMonth(), Month.JUNE.asShortMonth(), Month.JULY.asShortMonth(), Month.AUGUST.asShortMonth(), Month.SEPTEMBER.asShortMonth(), Month.OCTOBER.asShortMonth(),
            Month.NOVEMBER.asShortMonth(), Month.DECEMBER.asShortMonth())

        val intNames = listOf(1,2,3,4,5,6).map {
            it.toString()
        }


        Log.i("StatsFrag", "profits: $profitList")
        Log.i("StatsFrag", "expenses: $expenseList")

        for (m in monthNames){
            profits.add(BarEntry(0F, 0F))
            expenses.add(BarEntry(0F, 0F))
        }

        for (l in profitList){
            profits.add(BarEntry((l.month!!.value-1).toFloat(), l.totalProfit.toFloat()))
        }

        for (e in expenseList) {
            expenses.add(BarEntry((e.month!!.value)-0.5.toFloat(), e.totalProfit.toFloat()))
        }

        val dataSet = BarDataSet(profits, "Ежемесячный доход")
        val expensesDataSet = BarDataSet(expenses, "Расход")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.green)
        expensesDataSet.color = ContextCompat.getColor(requireContext(), R.color.red)

        val datasets = listOf(dataSet, expensesDataSet)

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(monthNames)
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            isGranularityEnabled = true
            granularity = 1f
            labelRotationAngle = 0f
            axisMinimum = 0f

        }
        chart.axisRight.isEnabled = false

        chart.apply {
            isDragXEnabled = true
            isDragYEnabled = true
            setVisibleXRangeMaximum(3f)
            data = BarData(datasets)
            data.barWidth = 0.5f
            description.text = ""
            animateY(1000)
            invalidate()
        }

    }

}