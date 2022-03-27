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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddFuzzySetBinding.inflate(inflater, container, false)

        binding.addBtn.setOnClickListener {
            val pointA = binding.aEdit.text.toString()
            val pointB = binding.bEdit.text.toString()
            val pointC = binding.cEdit.text.toString()
            val pointD = binding.dEdit.text.toString()
            val valueX = binding.xEdit.text.toString()


            if (checkFields(pointA, pointB, pointC, pointD)) {
                setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to listOf(pointA , pointB, pointC, pointD, valueX).toTypedArray()))
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

    private fun checkFields(pointA: String, pointB: String, pointC: String, pointD : String) : Boolean {
        if (pointA.isBlank() || pointB.isBlank() || pointC.isBlank() || pointD.isBlank()) {
            showSnackBarEmptyFieldFound()
            return false
        }
        return true
    }

    private fun showSnackBarEmptyFieldFound() {
        Snackbar.make(binding.addBtn, R.string.empty_field_found, Snackbar.LENGTH_LONG)
            .show()
    }

    companion object {
        const val REQUEST_KEY = "ADD_FUZZY_SET"
        const val BUNDLE_KEY = "FUZZY_SET"
    }
}