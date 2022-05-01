package com.chocomiruku.fuzzylogiclabworks

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.chocomiruku.fuzzylogiclabworks.databinding.FragmentChartBinding
import com.chocomiruku.fuzzylogiclabworks.fuzzy_util.LinguisticVariable
import com.chocomiruku.fuzzylogiclabworks.fuzzy_util.Trapezoid
import com.chocomiruku.fuzzylogiclabworks.fuzzy_util.Triangle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import java.util.*


class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private val laborCostsObjects =
        mutableListOf(
            22.2f, 64f, 25.5f, 43.2f, 87.6f, 122.3f, 165.5f, 52.9f, 37.4f, 65.8f,
            77.4f, 36.5f, 74.3f, 48.3f, 184f, 93.6f, 110.4f, 113.6f, 49f, 63f
        )
    private var linguisticVariables = mutableListOf<LinguisticVariable>(
        Triangle(20f, 35f, 44.5f, "Низкие"),
        Trapezoid(35.2f, 68.5f, 92.3f, 101f, "Средние"),
        Triangle(83.7f, 158.8f, 190.7f, "Высокие")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        styleChart(binding.linguisticVariablesChart)
        styleLinguisticVariablesAxis()
        addDefaultLinguisticVariablesToChart()
        updateBasePointsList()

        setFragmentResultListener(AddLinguisticVariableDialog.REQUEST_KEY) { _, bundle ->
            val triangleSet: Triangle? =
                bundle.getParcelable(AddLinguisticVariableDialog.TRIANGLE_BUNDLE_KEY)

            if (triangleSet != null) {
                addLinguisticVariableToChart(triangleSet)
                updateLinguisticVariableData(triangleSet)
            } else {
                val trapezoidSet: Trapezoid? =
                    bundle.getParcelable(AddLinguisticVariableDialog.TRAPEZOID_BUNDLE_KEY)
                trapezoidSet?.let {
                    addLinguisticVariableToChart(trapezoidSet)
                    updateLinguisticVariableData(trapezoidSet)
                }
            }
        }

        binding.addLinguisticVariableBtn.setOnClickListener {
            findNavController().navigate(
                ChartFragmentDirections.actionChartFragmentToAddLinguisticVariableDialog()
            )
        }

        binding.addBtn.setOnClickListener {
            if (binding.fuzzyTimeSeriesValueEdit.text.isNullOrBlank()) {
                showSnackBarEnterValue()
                return@setOnClickListener
            }

            val cost = binding.fuzzyTimeSeriesValueEdit.text.toString().toFloat()

            laborCostsObjects.add(cost)
            binding.baseData.text =
                binding.baseData.text.toString().plus(", ")
                    .plus(cost.toString())

            binding.fuzzyTimeSeriesValueEdit.text.clear()
        }

        binding.runBtn.setOnClickListener {
            updateCrispValuesChart()
            updateFuzzyValuesChart()
            updateTrendsChart()

            binding.crispValuesChart.isVisible = true
            binding.crispValuesChartText.isVisible = true

            binding.fuzzyValuesChart.isVisible = true
            binding.fuzzyValuesChart.isVisible = true

            binding.trendsChart.isVisible = true
            binding.trendsChartText.isVisible = true
        }

        return binding.root
    }

    private fun updateLinguisticVariableData(lingVariable: LinguisticVariable) {
        val names = linguisticVariables.map { it.name }

        if (lingVariable.name !in names) {
            linguisticVariables.add(lingVariable)
        }

        linguisticVariables.sortWith(compareBy({ it.a }, { it.c }))
    }

    private fun addLinguisticVariableToChart(lingVariable: LinguisticVariable) {
        val values = mutableListOf<Entry>()

        when (lingVariable) {
            is Triangle -> {
                values.apply {
                    add(Entry(lingVariable.a, 0F))
                    add(Entry(lingVariable.b, 1F))
                    add(Entry(lingVariable.c, 0F))
                }
            }
            is Trapezoid -> {
                values.apply {
                    add(Entry(lingVariable.a, 0F))
                    add(Entry(lingVariable.b, 1F))
                    add(Entry(lingVariable.c, 1F))
                    add(Entry(lingVariable.d, 0F))
                }
            }
        }

        val set = LineDataSet(values, lingVariable.name)
        val random = Random()
        val randomColor =
            Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))

        set.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            fillAlpha = 100
            setDrawFilled(true)
            lineWidth = 2f
            setDrawValues(false)
            color = randomColor
            fillColor = randomColor
        }

        binding.linguisticVariablesChart.data?.let {
            it.addDataSet(set)
            binding.linguisticVariablesChart.notifyDataSetChanged()
            binding.linguisticVariablesChart.invalidate()
        } ?: run {
            val dataSets = mutableListOf<ILineDataSet>()
            dataSets.add(set)
            val data = LineData(dataSets)
            binding.linguisticVariablesChart.data = data
            binding.linguisticVariablesChart.invalidate()
        }
    }

    private fun updateCrispValuesChart() {
        binding.crispValuesChart.data?.let {
            it.clearValues()
            binding.crispValuesChart.invalidate()
            binding.crispValuesChart.clear()
        }

        styleChart(binding.crispValuesChart)
        styleCrispValuesAxis()

        val values = mutableListOf<Entry>()

        for (i in laborCostsObjects.indices) {
            values.add(Entry(i.toFloat(), laborCostsObjects[i]))
        }

        val set = LineDataSet(values, "Чёткие значения")
        val random = Random()
        val randomColor =
            Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))

        set.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = 4f
            setDrawValues(false)
            color = randomColor
        }

        val dataSets = mutableListOf<ILineDataSet>()
        dataSets.add(set)
        val data = LineData(dataSets)
        binding.crispValuesChart.data = data
        binding.crispValuesChart.invalidate()
    }

    private fun updateFuzzyValuesChart() {
        binding.fuzzyValuesChart.data?.let {
            it.clearValues()
            binding.fuzzyValuesChart.invalidate()
            binding.fuzzyValuesChart.clear()
        }

        styleChart(binding.fuzzyValuesChart)
        styleFuzzyValuesAxis()

        val values = mutableListOf<Entry>()

        for (i in laborCostsObjects.indices) {
            val result = getDegreeOfBelonging(linguisticVariables, laborCostsObjects[i])

            val linguisticVariableIndex =
                linguisticVariables.indexOfFirst { it.name == result.name }
            values.add(Entry(i.toFloat(), linguisticVariableIndex.toFloat()))
        }

        val set = LineDataSet(values, "Нечёткие значения")
        val random = Random()
        val randomColor =
            Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))

        set.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = 4f
            setDrawValues(false)
            color = randomColor
        }

        val dataSets = mutableListOf<ILineDataSet>()
        dataSets.add(set)
        val data = LineData(dataSets)
        binding.fuzzyValuesChart.data = data
        binding.fuzzyValuesChart.invalidate()
    }

    private fun updateTrendsChart() {
        binding.trendsChart.data?.let {
            it.clearValues()
            binding.trendsChart.invalidate()
            binding.trendsChart.clear()
        }

        styleTrendsChart()

        val growthValues = mutableListOf<BarEntry>()
        val reductionValues = mutableListOf<BarEntry>()
        val stagnationValues = mutableListOf<BarEntry>()

        for (i in 1 until laborCostsObjects.size) {
            val currentResult =
                getDegreeOfBelonging(linguisticVariables, laborCostsObjects[i])
            val previousResult =
                getDegreeOfBelonging(linguisticVariables, laborCostsObjects[i - 1])

            val linguisticVariableCurrent =
                linguisticVariables.indexOfFirst { it.name == currentResult.name }.toFloat()
            val linguisticVariablePrevious =
                linguisticVariables.indexOfFirst { it.name == previousResult.name }.toFloat()

            val trendValue = linguisticVariableCurrent - linguisticVariablePrevious
            when {
                trendValue < 0 -> {
                    reductionValues.add(
                        BarEntry(
                            i.toFloat(),
                            linguisticVariableCurrent - linguisticVariablePrevious
                        )
                    )
                }
                trendValue > 0 -> {
                    growthValues.add(
                        BarEntry(
                            i.toFloat(),
                            linguisticVariableCurrent - linguisticVariablePrevious
                        )
                    )
                }
                else -> stagnationValues.add(
                    BarEntry(
                        i.toFloat(),
                        linguisticVariableCurrent - linguisticVariablePrevious + 0.05f
                    )
                )
            }
        }

        val growthSet = BarDataSet(growthValues, "Рост")
        growthSet.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            setDrawValues(false)
            color = requireContext().getColor(R.color.green_200)
        }

        val reductionSet = BarDataSet(reductionValues, "Падение")
        reductionSet.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            setDrawValues(false)
            color = requireContext().getColor(R.color.pink_600)
        }

        val stagnationSet = BarDataSet(stagnationValues, "Стагнация")
        stagnationSet.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            setDrawValues(false)
            color = requireContext().getColor(R.color.gray_400)
        }

        val dataSets = mutableListOf<IBarDataSet>()
        dataSets.apply {
            add(growthSet)
            add(reductionSet)
            add(stagnationSet)
        }
        val data = BarData(dataSets)
        binding.trendsChart.data = data
        binding.trendsChart.invalidate()
    }

    private fun styleChart(chart: LineChart) {
        chart.apply {
            setTouchEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            setDrawBorders(true)
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isWordWrapEnabled = true
            legend.xEntrySpace = 8F
            legend.xOffset = 3F
        }
    }

    private fun styleLinguisticVariablesAxis() {
        val chart = binding.linguisticVariablesChart

        val yAxis = chart.axisLeft
        yAxis.apply {
            axisMinimum = 0F
            axisMaximum = 1.1F
            setDrawTopYLabelEntry(true)
            setDrawGridLines(true)
            isGranularityEnabled = true
            labelCount = 3
            granularity = 0.5F
        }

        val xAxis = chart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            axisMinimum = 0F
            labelCount = 10
            setDrawGridLines(true)
        }
    }

    private fun styleCrispValuesAxis() {
        val chart = binding.crispValuesChart

        val yAxis = chart.axisLeft
        yAxis.apply {
            setDrawTopYLabelEntry(true)
            setDrawGridLines(true)
        }

        val xAxis = chart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            axisMinimum = 0F
            axisMaximum = laborCostsObjects.size.toFloat()
            labelCount = (laborCostsObjects.size + 1)
            setDrawGridLines(true)
        }
    }

    private fun styleTrendsChart() {
        val chart = binding.trendsChart

        chart.apply {
            setTouchEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            setDrawBorders(true)
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isWordWrapEnabled = true
            legend.xEntrySpace = 8F
            legend.xOffset = 3F
        }

        val yAxis = chart.axisLeft
        yAxis.apply {
            setDrawTopYLabelEntry(true)
            setDrawGridLines(true)
        }

        val xAxis = chart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            axisMinimum = 0F
            axisMaximum = laborCostsObjects.size.toFloat()
            labelCount = (laborCostsObjects.size + 1)
            setDrawGridLines(true)
        }
    }

    private fun styleFuzzyValuesAxis() {
        val chart = binding.fuzzyValuesChart

        val yAxis = chart.axisLeft
        yAxis.apply {
            setDrawTopYLabelEntry(true)
            setDrawGridLines(true)
            axisMinimum = 0f
            valueFormatter =
                IndexAxisValueFormatter(linguisticVariables.map { it.name }.toTypedArray())
            isGranularityEnabled = true
            granularity = 1f
            labelCount = linguisticVariables.size
        }

        val xAxis = chart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            axisMinimum = 0F
            axisMaximum = laborCostsObjects.size.toFloat()
            labelCount = (laborCostsObjects.size + 1)
            setDrawGridLines(true)
        }
    }

    private fun addDefaultLinguisticVariablesToChart() {
        for (set in linguisticVariables) {
            addLinguisticVariableToChart(set)
        }
    }

    private fun updateBasePointsList() {
        var laborCostsString = ""
        for (cost in laborCostsObjects) {
            laborCostsString += cost.toString().plus(", ")
        }

        binding.baseData.text = laborCostsString.dropLast(2)
    }

    private fun showSnackBarEnterValue() {
        Snackbar.make(
            binding.addFuzzyTimeSeriesValuesLabel,
            R.string.enter_fuzzy_time_series_value,
            Snackbar.LENGTH_LONG
        )
            .show()
    }
}