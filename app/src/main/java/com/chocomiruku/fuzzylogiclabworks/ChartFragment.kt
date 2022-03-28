package com.chocomiruku.fuzzylogiclabworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.chocomiruku.fuzzylogiclabworks.databinding.FragmentChartBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar


class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    private var fuzzySetsCount = 0
    private var fuzzySetFirst = mutableListOf<String>()
    private var fuzzySetSecond = mutableListOf<String>()
    private var fuzzySetThird = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        styleChart()

        setFragmentResultListener(AddFuzzySetDialog.REQUEST_KEY) { _, bundle ->
            val fuzzySetPoints = bundle.getStringArray(AddFuzzySetDialog.BUNDLE_KEY)
            fuzzySetPoints?.let {
                when (fuzzySetsCount) {
                    0 -> {
                        fuzzySetFirst = fuzzySetPoints.toMutableList()
                        addFuzzySetsData(fuzzySetFirst)
                        fuzzySetsCount++
                    }
                    1 -> {
                        fuzzySetSecond = fuzzySetPoints.toMutableList()
                        addFuzzySetsData(fuzzySetSecond)
                        binding.findIntersectionBtn.visibility = View.VISIBLE
                        fuzzySetsCount++
                    }
                    else -> {
                        fuzzySetThird = fuzzySetPoints.toMutableList()
                        addFuzzySetsData(fuzzySetThird)
                        binding.addFuzzySetBtn.visibility = View.GONE
                        fuzzySetsCount++
                    }
                }
            }
        }

        binding.addFuzzySetBtn.setOnClickListener {
            findNavController().navigate(
                ChartFragmentDirections.actionChartFragmentToAddFuzzySetDialog()
            )
        }

        binding.findIntersectionBtn.setOnClickListener {
            addIntersectionToChart()
        }

        binding.resetBtn.setOnClickListener {
            fuzzySetsCount = 0
            binding.basePointsText.visibility = View.GONE
            binding.fuzzySet1.visibility = View.GONE
            binding.fuzzySet2.visibility = View.GONE
            binding.fuzzySet3.visibility = View.GONE
            binding.findIntersectionBtn.visibility = View.GONE
            binding.addFuzzySetBtn.visibility = View.VISIBLE
            binding.fuzzySetsChart.data?. let {
                binding.fuzzySetsChart.data.clearValues()
                binding.fuzzySetsChart.invalidate()
                binding.fuzzySetsChart.clear()
            }
        }

        return binding.root
    }

    private fun addFuzzySetsData(fuzzySetPointsStrings: MutableList<String>) {
        val fuzzySetPointsFloat = fuzzySetPointsStrings.map { it.toFloat() }
        val xValue = fuzzySetPointsFloat[4]
        val mValue = getDegreeOfBelonging(fuzzySetPointsFloat)

        when (fuzzySetsCount) {
            0 -> {
                binding.a1Text.text = getString(R.string.a).plus(" " + fuzzySetPointsStrings[0])
                binding.b1Text.text = getString(R.string.b).plus(" " + fuzzySetPointsStrings[1])
                binding.c1Text.text = getString(R.string.c).plus(" " + fuzzySetPointsStrings[2])
                binding.d1Text.text = getString(R.string.d).plus(" " + fuzzySetPointsStrings[3])
                binding.m1Text.text = getString(R.string.m).plus(" μ($xValue) = $mValue")

                binding.fuzzySet1.visibility = View.VISIBLE
                binding.basePointsText.visibility = View.VISIBLE
                addDataToChart(fuzzySetPointsFloat)
            }
            1 -> {
                binding.a2Text.text = getString(R.string.a).plus(" " + fuzzySetPointsStrings[0])
                binding.b2Text.text = getString(R.string.b).plus(" " + fuzzySetPointsStrings[1])
                binding.c2Text.text = getString(R.string.c).plus(" " + fuzzySetPointsStrings[2])
                binding.d2Text.text = getString(R.string.d).plus(" " + fuzzySetPointsStrings[3])
                binding.m2Text.text = getString(R.string.m).plus(" μ($xValue) = $mValue")

                binding.fuzzySet2.visibility = View.VISIBLE
                addDataToChart(fuzzySetPointsFloat)
            }
            else -> {
                binding.a3Text.text = getString(R.string.a).plus(" " + fuzzySetPointsStrings[0])
                binding.b3Text.text = getString(R.string.b).plus(" " + fuzzySetPointsStrings[1])
                binding.c3Text.text = getString(R.string.c).plus(" " + fuzzySetPointsStrings[2])
                binding.d3Text.text = getString(R.string.d).plus(" " + fuzzySetPointsStrings[3])
                binding.m3Text.text = getString(R.string.m).plus(" μ($xValue) = $mValue")

                binding.fuzzySet3.visibility = View.VISIBLE
                addDataToChart(fuzzySetPointsFloat)
            }
        }
    }

    private fun addDataToChart(fuzzySetPoints: List<Float>) {

        val values = mutableListOf<Entry>()
        values.apply {
            add(Entry(fuzzySetPoints[0], 0F))
            add(Entry(fuzzySetPoints[1], 1F))
            add(Entry(fuzzySetPoints[2], 1F))
            add(Entry(fuzzySetPoints[3], 0F))
        }

        val set = LineDataSet(values, "")

        set.axisDependency = YAxis.AxisDependency.LEFT
        set.fillAlpha = 100
        set.setDrawFilled(true)
        set.lineWidth = 2f
        set.setDrawValues(false)

        set.apply {
            when (fuzzySetsCount) {
                0 -> {
                    label = "Множество 1"
                    color = requireContext().getColor(R.color.green_200)
                    fillColor = requireContext().getColor(R.color.green_200)
                }
                1 -> {
                    label = "Множество 2"
                    color = requireContext().getColor(R.color.violet_500)
                    fillColor = requireContext().getColor(R.color.violet_500)
                }
                else -> {
                    label = "Множество 3"
                    color = requireContext().getColor(R.color.green_500)
                    fillColor = requireContext().getColor(R.color.green_500)
                }
            }
        }

        updateXAxisMaximum(fuzzySetPoints.dropLast(1).maxOrNull())

        binding.fuzzySetsChart.data?.let {
            binding.fuzzySetsChart.data.addDataSet(set)
            binding.fuzzySetsChart.notifyDataSetChanged()
            binding.fuzzySetsChart.invalidate()
        } ?: run {
            val dataSets = mutableListOf<ILineDataSet>()
            dataSets.add(set)
            val data = LineData(dataSets)
            binding.fuzzySetsChart.data = data
            binding.fuzzySetsChart.invalidate()
        }
    }

    private fun addIntersectionToChart() {
        val intersectionPoints: List<Pair<Float, Float>>?

        try {
            intersectionPoints = when (fuzzySetsCount) {
                2 -> {
                    val trapezoids = listOf(fuzzySetFirst.dropLast(1).map { it.toFloat() }, fuzzySetSecond.map { it.toFloat() })
                    findIntersection(trapezoids)
                }
                else -> {
                    val trapezoids = listOf(fuzzySetFirst.dropLast(1).map { it.toFloat() }, fuzzySetSecond.map { it.toFloat() }, fuzzySetThird.map { it.toFloat() })
                    findIntersection(trapezoids)
                }
            }

            val values = mutableListOf<Entry>()

            for (point in intersectionPoints) {
                values.add(Entry(point.first, point.second))
            }

            val set = LineDataSet(values, "Пересечение")
            set.apply {
                axisDependency = YAxis.AxisDependency.LEFT
                fillAlpha = 100
                setDrawFilled(true)
                lineWidth = 2f
                setDrawValues(false)
                color = requireContext().getColor(R.color.pink_600)
                fillColor = requireContext().getColor(R.color.pink_600)
            }

            binding.fuzzySetsChart.data.addDataSet(set)
            binding.fuzzySetsChart.notifyDataSetChanged()
            binding.fuzzySetsChart.invalidate()

        } catch (e: Exception) {
            showSnackSetsDontIntersect()
        }
    }

    private fun styleChart() {
        val chart = binding.fuzzySetsChart

        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.setDrawGridBackground(false)
        chart.setDrawBorders(true)
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.legend.isWordWrapEnabled = true
        chart.legend.xEntrySpace = 8F
        chart.legend.xOffset = 3F

        val yAxis = chart.axisLeft
        yAxis.axisMinimum = 0F
        yAxis.axisMaximum = 1.1F
        yAxis.setDrawTopYLabelEntry(false)
        yAxis.setDrawGridLines(true)
        yAxis.isGranularityEnabled = true
        yAxis.labelCount = 10
        yAxis.granularity = 0.1F


        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0F
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1F
        xAxis.labelCount = 20
        xAxis.setDrawGridLines(true)
    }

    private fun updateXAxisMaximum(maxValue: Float?) {
        maxValue?.let {
            val xAxis = binding.fuzzySetsChart.xAxis
            if (maxValue > xAxis.axisMaximum) {
                xAxis.axisMaximum = maxValue + 5F
            }
        }
    }

    private fun showSnackSetsDontIntersect() {
        Snackbar.make(binding.findIntersectionBtn, R.string.sets_dont_intersect, Snackbar.LENGTH_LONG)
            .show()
    }
}