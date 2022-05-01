package com.chocomiruku.fuzzylogiclabworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.chocomiruku.fuzzylogiclabworks.databinding.DialogAddLinguisticVariableBinding
import com.chocomiruku.fuzzylogiclabworks.fuzzy_util.Trapezoid
import com.chocomiruku.fuzzylogiclabworks.fuzzy_util.Triangle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class AddLinguisticVariableDialog : BottomSheetDialogFragment() {
    private var _binding: DialogAddLinguisticVariableBinding? = null
    private val binding get() = _binding!!
    private var functionType: FunctionType = FunctionType.TRIANGLE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddLinguisticVariableBinding.inflate(inflater, container, false)

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_button_trapezoid -> {
                    functionType = FunctionType.TRAPEZOID
                    binding.pointDLayout.visibility = View.VISIBLE
                }
                R.id.radio_button_triangle -> {
                    functionType = FunctionType.TRIANGLE
                    binding.pointDLayout.visibility = View.GONE
                }
            }
        }

        binding.addBtn.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            val pointA = binding.aEdit.text.toString()
            val pointB = binding.bEdit.text.toString()
            val pointC = binding.cEdit.text.toString()

            when (functionType) {
                FunctionType.TRIANGLE -> {
                    if (checkFields(
                            name = name,
                            pointA = pointA,
                            pointB = pointB,
                            pointC = pointC
                        )
                    ) {
                        setFragmentResult(
                            REQUEST_KEY,
                            bundleOf(
                                TRIANGLE_BUNDLE_KEY to Triangle(
                                    pointA.toFloat(),
                                    pointB.toFloat(),
                                    pointC.toFloat(),
                                    name
                                )
                            )
                        )
                        findNavController().navigateUp()
                    }
                }
                else -> {
                    val pointD = binding.dEdit.text.toString()
                    if (checkFields(
                            name = name,
                            pointA = pointA,
                            pointB = pointB,
                            pointC = pointC,
                            pointD = pointD
                        )
                    ) {
                        setFragmentResult(
                            REQUEST_KEY,
                            bundleOf(
                                TRAPEZOID_BUNDLE_KEY to Trapezoid(
                                    pointA.toFloat(),
                                    pointB.toFloat(),
                                    pointC.toFloat(),
                                    pointD.toFloat(),
                                    name
                                )
                            )
                        )
                        findNavController().navigateUp()
                    }
                }
            }

        }

        return binding.root
    }

    private fun checkFields(
        name: String,
        pointA: String,
        pointB: String,
        pointC: String,
        pointD: String? = null
    ): Boolean {
        if (name.isBlank() || pointA.isBlank() || pointB.isBlank() || pointC.isBlank()) {
            showSnackBarEmptyFieldFound()
            return false
        }
        pointD?.let {
            if (pointD.isBlank()) {
                showSnackBarEmptyFieldFound()
                return false
            }
        }
        return true
    }

    private fun showSnackBarEmptyFieldFound() {
        dialog?.window?.decorView
            ?.let { Snackbar.make(it, R.string.empty_field_found, Snackbar.LENGTH_LONG).show() }
    }

    companion object {
        const val REQUEST_KEY = "ADD_MEMBERSHIP_FUNCTION"
        const val TRIANGLE_BUNDLE_KEY = "TRIANGLE"
        const val TRAPEZOID_BUNDLE_KEY = "TRAPEZOID"

        enum class FunctionType { TRIANGLE, TRAPEZOID }
    }
}