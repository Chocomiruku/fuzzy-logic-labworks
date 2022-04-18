package com.chocomiruku.fuzzylogiclabworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.chocomiruku.fuzzylogiclabworks.databinding.DialogClusteringParametersBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar


class ClusteringParametersDialog : BottomSheetDialogFragment() {
    private var _binding: DialogClusteringParametersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogClusteringParametersBinding.inflate(inflater, container, false)

        binding.okBtn.setOnClickListener {
            val clustersCount = binding.clustersCountEdit.text.toString()
            val fuzzinessDegree = binding.fuzzinessDegreeEdit.text.toString()
            val calculationsPrecision = binding.calcPrecisionEdit.text.toString()
            val maxIterationsCount = binding.maxIterationsCountEdit.text.toString()

            if (checkFields(
                    clustersCount = clustersCount,
                    fuzzinessDegree = fuzzinessDegree,
                    calculationsPrecision = calculationsPrecision,
                    maxIterationsCount = maxIterationsCount
                )
            ) {
                setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        BUNDLE_KEY to listOf(
                            clustersCount,
                            fuzzinessDegree,
                            calculationsPrecision,
                            maxIterationsCount
                        ).toTypedArray()
                    )
                )
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

    private fun checkFields(
        clustersCount: String,
        fuzzinessDegree: String,
        calculationsPrecision: String,
        maxIterationsCount: String
    ): Boolean {
        if (clustersCount.isBlank() || fuzzinessDegree.isBlank() || calculationsPrecision.isBlank() || maxIterationsCount.isBlank()) {
            showSnackBarError(R.string.empty_field_found)
            return false
        }
        when {
            clustersCount.toInt() <= 1 -> {
                showSnackBarError(R.string.clusters_count_incorrect)
                return false
            }
            fuzzinessDegree.toFloat() <= 1 -> {
                showSnackBarError(R.string.fuzziness_degree_incorrect)
                return false
            }
            calculationsPrecision.toFloat() >= 1 -> {
                showSnackBarError(R.string.calculations_precision_incorrect)
                return false
            }
            maxIterationsCount.toInt() <= 1 -> {
                showSnackBarError(R.string.max_iterations_count_incorrect)
                return false
            }
            else -> return true
        }
    }

    private fun showSnackBarError(messageResId: Int) {
        dialog?.window?.decorView
            ?.let { Snackbar.make(it, messageResId, Snackbar.LENGTH_LONG).show() }
    }

    companion object {
        const val REQUEST_KEY = "ADD_CLUSTERING_PARAMETERS"
        const val BUNDLE_KEY = "CLUSTERING_PARAMETERS"
    }
}