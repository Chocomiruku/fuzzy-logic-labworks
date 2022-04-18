package com.chocomiruku.fuzzylogiclabworks

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.chocomiruku.fuzzylogiclabworks.databinding.FragmentChartBinding
import com.chocomiruku.fuzzylogiclabworks.fuzzy_clustering.ClusterSystem
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    private var clusterSystem: ClusterSystem? = null
    private val heightsObjects =
        mutableListOf(165f, 170f, 182f, 193f, 169f, 172f, 188f, 178f, 164f, 190f)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        styleChart()
        updateParameters()
        updateBasePointsList()

        setFragmentResultListener(ClusteringParametersDialog.REQUEST_KEY) { _, bundle ->
            val parameters = bundle.getStringArray(ClusteringParametersDialog.BUNDLE_KEY)
            parameters?.let {
                updateParameters(parameters.toMutableList())
            }
        }

        binding.runFcmAlgorithmBtn.setOnClickListener {
            clusterSystem = ClusterSystem(
                heightsObjects,
                clustersCount,
                fuzzinessDegree,
                calculationsPrecision,
                maxIterationsCount
            )
            clusterSystem?.runFCMAlgorithm()
            addDataToChart()
        }

        binding.changeClusteringParametersBtn.setOnClickListener {
            findNavController().navigate(
                ChartFragmentDirections.actionChartFragmentToClusteringParametersDialog()
            )
        }

        binding.addBtn.setOnClickListener {
            binding.heightObjectEdit.text?.let {
                val height = it.toString().toFloat()

                if (!heightsObjects.contains(height)) {
                    heightsObjects.add(height)
                    binding.baseData.text =
                        binding.baseData.text.toString().plus(", ").plus(height.toInt().toString())
                            .plus(" см")
                }

                it.clear()
            }
        }

        return binding.root
    }

    private fun addDataToChart() {
        binding.clusteringChart.data?.let {
            binding.clusteringChart.data.clearValues()
            binding.clusteringChart.invalidate()
            binding.clusteringChart.clear()
        }

        clusterSystem?.let { system ->
            val degreesOfBelongingMatrix = system.degreesOfBelongingMatrix

            for (i in 0 until system.clustersCount) {
                val values = mutableListOf<Entry>()

                for (j in degreesOfBelongingMatrix.indices) {
                    values.add(Entry(heightsObjects[j], degreesOfBelongingMatrix[j][i]))
                }

                val set = LineDataSet(values.sortedBy { it.x }, "Кластер ${i + 1}")
                val randomColor =
                    Color.argb(255, (0..256).random(), (0..256).random(), (0..256).random())

                set.apply {
                    axisDependency = YAxis.AxisDependency.LEFT
                    lineWidth = 3f
                    setDrawValues(false)
                    color = randomColor
                }

                updateXAxisBorders()

                binding.clusteringChart.data?.let {
                    binding.clusteringChart.data.addDataSet(set)
                    binding.clusteringChart.notifyDataSetChanged()
                    binding.clusteringChart.invalidate()
                } ?: run {
                    val dataSets = mutableListOf<ILineDataSet>()
                    dataSets.add(set)
                    val data = LineData(dataSets)
                    binding.clusteringChart.data = data
                    binding.clusteringChart.invalidate()
                }
            }
        }
    }

    private fun updateParameters(parameters: List<String>? = null) {
        parameters?.let {
            clustersCount = parameters[0].toInt()
            binding.clustersCountText.text = parameters[0]

            fuzzinessDegree = parameters[1].toFloat()
            binding.fuzzinessDegreeText.text = parameters[1]

            calculationsPrecision = parameters[2].toFloat()
            binding.calcPrecisionText.text = parameters[2]

            maxIterationsCount = parameters[3].toInt()
            binding.maxIterationsCountText.text = parameters[3]
        } ?: run {
            binding.clustersCountText.text = clustersCount.toString()
            binding.fuzzinessDegreeText.text = fuzzinessDegree.toString()
            binding.calcPrecisionText.text = calculationsPrecision.toString()
            binding.maxIterationsCountText.text = maxIterationsCount.toString()
        }
    }

    private fun updateBasePointsList() {
        var heightsString = ""
        for (height in heightsObjects) {
            heightsString += height.toInt().toString().plus(" см, ")
        }

        binding.baseData.text = heightsString.dropLast(2)
    }


    private fun styleChart() {
        val chart = binding.clusteringChart

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
        xAxis.axisMinimum = heightsObjects.minOrNull()!! - 5f
        xAxis.axisMaximum = heightsObjects.maxOrNull()!! + 5f
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 5F
        xAxis.labelCount = 10
        xAxis.setDrawGridLines(true)
    }

    private fun updateXAxisBorders() {
        val minValue = heightsObjects.minOrNull()
        val maxValue = heightsObjects.maxOrNull()

        val xAxis = binding.clusteringChart.xAxis
        maxValue?.let {
            if (maxValue > xAxis.axisMaximum) {
                xAxis.axisMaximum = maxValue + 5F
            }
        }

        minValue?.let {
            if (minValue < xAxis.axisMinimum) {
                xAxis.axisMinimum = minValue - 5F
            }
        }
    }

    companion object {
        var clustersCount = 3
        var fuzzinessDegree = 1.6f
        var calculationsPrecision = 0.001f
        var maxIterationsCount = 100
    }
}