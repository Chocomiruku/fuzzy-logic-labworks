package com.chocomiruku.fuzzylogiclabworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.chocomiruku.fuzzylogiclabworks.databinding.DialogAddFuzzySetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class AddFuzzySetDialog : BottomSheetDialogFragment() {
    private var _binding: DialogAddFuzzySetBinding? = null
    private val binding get() = _binding!!
    private var functionType: FunctionType = FunctionType.TRIANGLE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddFuzzySetBinding.inflate(inflater, container, false)

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
                                BUNDLE_KEY to listOf(
                                    name,
                                    pointA,
                                    pointB,
                                    pointC
                                ).toTypedArray()
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
                                BUNDLE_KEY to listOf(
                                    name,
                                    pointA,
                                    pointB,
                                    pointC,
                                    pointD,
                                ).toTypedArray()
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
        const val REQUEST_KEY = "ADD_FUZZY_SET"
        const val BUNDLE_KEY = "FUZZY_SET"

        enum class FunctionType { TRIANGLE, TRAPEZOID }
    }
}