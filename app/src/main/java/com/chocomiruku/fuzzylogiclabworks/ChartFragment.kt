package com.chocomiruku.fuzzylogiclabworks

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import java.util.*


class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        styleChart()

        setFragmentResultListener(AddFuzzySetDialog.REQUEST_KEY) { _, bundle ->
            val fuzzySetPoints = bundle.getStringArray(AddFuzzySetDialog.BUNDLE_KEY)
            fuzzySetPoints?.let {
                addFuzzySetsData(fuzzySetPoints.toMutableList())

                binding.basePointsText.visibility = View.VISIBLE
                binding.table.visibility = View.VISIBLE
            }
        }

        binding.addFuzzySetBtn.setOnClickListener {
            findNavController().navigate(
                ChartFragmentDirections.actionChartFragmentToAddFuzzySetDialog()
            )
        }


        binding.resetBtn.setOnClickListener {
            binding.basePointsText.visibility = View.GONE
            binding.table.visibility = View.GONE
            binding.fuzzySetsChart.data?.let {
                binding.fuzzySetsChart.data.clearValues()
                binding.fuzzySetsChart.invalidate()
                binding.fuzzySetsChart.clear()
            }
        }

        return binding.root
    }

    private fun addFuzzySetsData(fuzzySetPointsStrings: MutableList<String>) {
        val fuzzySetName = fuzzySetPointsStrings[0]
        val fuzzySetPointsFloat = fuzzySetPointsStrings.drop(1).map { it.toFloat() }

        addDataToChart(fuzzySetName, fuzzySetPointsFloat)
        addDataInTable(fuzzySetPointsStrings)
    }

    private fun addDataToChart(name: String, fuzzySetPoints: List<Float>) {
        val values = mutableListOf<Entry>()
        when (fuzzySetPoints.size) {
            4 -> {
                values.apply {
                    add(Entry(fuzzySetPoints[0], 0F))
                    add(Entry(fuzzySetPoints[1], 1F))
                    add(Entry(fuzzySetPoints[2], 0F))
                }
            }
            5 -> {
                values.apply {
                    add(Entry(fuzzySetPoints[0], 0F))
                    add(Entry(fuzzySetPoints[1], 1F))
                    add(Entry(fuzzySetPoints[2], 1F))
                    add(Entry(fuzzySetPoints[3], 0F))
                }
            }
        }

        val set = LineDataSet(values, name)
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

        updateXAxisBorders(fuzzySetPoints)

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

    private fun addDataInTable(fuzzySetPointsStrings: MutableList<String>) {
        val fuzzySetName = fuzzySetPointsStrings[0]
        val fuzzySetPointsFloat = fuzzySetPointsStrings.drop(1).map { it.toFloat() }

        val table = binding.table
        val row = TableRow(requireContext())
        row.setPadding(10, 10, 10, 10)
        table.addView(row)

        val textViewName = TextView(requireContext())
        styleTextView(textViewName)
        textViewName.text = fuzzySetName
        textViewName.layoutParams =
            TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2F)
        row.addView(textViewName)

        val textViewPointA = TextView(requireContext())
        styleTextView(textViewPointA)
        textViewPointA.text = fuzzySetPointsStrings[1]
        row.addView(textViewPointA)

        val textViewPointB = TextView(requireContext())
        styleTextView(textViewPointB)
        textViewPointB.text = fuzzySetPointsStrings[2]
        row.addView(textViewPointB)

        val textViewPointC = TextView(requireContext())
        styleTextView(textViewPointC)
        textViewPointC.text = fuzzySetPointsStrings[3]
        row.addView(textViewPointC)

        val textViewPointD = TextView(requireContext())
        styleTextView(textViewPointD)

        val textViewValueM = TextView(requireContext())
        styleTextView(textViewValueM)
        textViewValueM.setOnClickListener {
            showSnackBarXValue(fuzzySetPointsFloat.last())
        }

        when (fuzzySetPointsStrings.size) {
            5 -> {
                textViewPointD.text = ""
                row.addView(textViewPointD)

                val mValue = getDegreeOfBelongingTriangle(fuzzySetPointsFloat)
                textViewValueM.text = mValue.toString()
                textViewValueM.layoutParams =
                    TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2F)
                row.addView(textViewValueM)
            }
            6 -> {
                textViewPointD.text = fuzzySetPointsStrings[4]
                row.addView(textViewPointD)

                val mValue = getDegreeOfBelongingTrapezoid(fuzzySetPointsFloat)
                textViewValueM.text = mValue.toString()
                textViewValueM.layoutParams =
                    TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2F)
                row.addView(textViewValueM)
            }
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
        xAxis.labelCount = 10
        xAxis.setDrawGridLines(true)
    }

    private fun styleTextView(textView: TextView) {
        textView.layoutParams =
            TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)

        textView.apply {
            background = ContextCompat.getDrawable(requireActivity(), R.drawable.item_border)
            gravity = Gravity.CENTER_HORIZONTAL
            setTextAppearance(R.style.table_default)
        }
    }

    private fun updateXAxisBorders(fuzzySetPoints: List<Float>) {
        val maxValue = fuzzySetPoints.dropLast(1).maxOrNull()

        val xAxis = binding.fuzzySetsChart.xAxis
        maxValue?.let {
            if (maxValue > xAxis.axisMaximum) {
                xAxis.axisMaximum = maxValue + 500F
                xAxis.labelCount = 10
            }
        }
    }

    private fun showSnackBarXValue(xValue: Float) {
        Snackbar.make(binding.table, "x: $xValue", Snackbar.LENGTH_LONG).show()
    }
}