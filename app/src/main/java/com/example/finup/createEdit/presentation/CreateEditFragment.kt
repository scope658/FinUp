package com.example.finup.createEdit.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.finup.R
import com.example.finup.core.ProvideViewModel
import com.example.finup.databinding.CreateEditPageBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch


class CreateEditFragment : Fragment(R.layout.create_edit_page) {

    private var _binding: CreateEditPageBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var materialDatePicker: MaterialDatePicker<Long>
    private lateinit var viewModel: CreateEditTransactionViewModel

    private val watcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            sumChangeFlow.value = s.toString()
        }
    }

    private val datePickerListener =
        MaterialPickerOnPositiveButtonClickListener<Long> { selection ->
            viewModel.selectDate(selection)
        }

    companion object {
        private const val SCREEN_TYPE_KEY = "ScreenTypeKey"
        private const val TRANSACTION_ID_KEY = "TransactionIdKey"
        private const val TRANSACTION_TYPE_KEY = "TransactionTypeKey"

        fun newInstance(
            screenType: String,
            transactionId: Long,
            transactionType: String
        ): Fragment {
            val fragment = CreateEditFragment()
            fragment.arguments = Bundle().apply {
                putString(SCREEN_TYPE_KEY, screenType)
                putLong(TRANSACTION_ID_KEY, transactionId)
                putString(TRANSACTION_TYPE_KEY, transactionType)
            }
            return fragment
        }
    }

    private val sumChangeFlow = MutableStateFlow("")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("fatal", "OnViewCreated")
        _binding = CreateEditPageBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            sumChangeFlow
                .drop(1)
                .debounce(500L)
                .collect { sumString ->
                    val sum = sumString.toIntOrNull() ?: 0
                    viewModel.updateSum(sum)
                }
        }
        val screenType = requireArguments().getString(SCREEN_TYPE_KEY)
        val transactionId = requireArguments().getLong(TRANSACTION_ID_KEY)
        val transactionType = requireArguments().getString(TRANSACTION_TYPE_KEY)!!

        viewModel = (activity as ProvideViewModel).getViewModel(
            this,
            CreateEditTransactionViewModel::class.java
        )

        val key = "$screenType $transactionType"
        val titleResId = when (key) {
            "Create Expense" -> R.string.CreateExpensePageTitle
            "Create Income" -> R.string.CreateIncomePageTitle
            "Edit Expense" -> R.string.EditExpenseTitle
            "Edit Income" -> R.string.EditIncomeTitle
            else -> throw IllegalStateException("screen is not found: $key")
        }

        val currentTitle = getString(titleResId)
        if (screenType == "Create") {
            viewModel.createInit(currentTitle)
        } else {
            viewModel.editInit(currentTitle, transactionId, transactionType)
        }

        val categories = if (transactionType == "Expense") {
            listOf(
                binding.utilitiesButton,
                binding.transfersButton,
                binding.groceriesButton,
                binding.otherButton,
            )
        } else {
            listOf(
                binding.kaspiButton,
                binding.bccButton,
            )
        }

        categories.forEach { button ->
            button.setOnClickListener {
                viewModel.selectCategory(button.text.toString())
            }
        }

        materialDatePicker = datePicker()
            .setTitleText(getString(R.string.dateTitle))
            .build()
        materialDatePicker.addOnPositiveButtonClickListener(datePickerListener)

        binding.saveButton.setOnClickListener {

            if (screenType == "Edit") {
                viewModel.edit(transactionId, transactionType)
            } else {
                viewModel.create(transactionType)
            }
        }

        binding.backButton.setOnClickListener {
            viewModel.comeback()
        }
        binding.deleteButton.setOnClickListener {
            viewModel.delete(transactionId)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.comeback()
        }

        binding.openDateButton.setOnClickListener {
            materialDatePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER_TAG")
        }

        viewModel.uiStateLiveData().observe(viewLifecycleOwner) {
            it.show(categories, binding.titleTextView, binding.deleteButton,binding.sumInputEditText)
        }

        viewModel.stateLiveData().observe(viewLifecycleOwner) { state ->
            binding.saveButton.isEnabled = state.checkIsValid()
            binding.dateTextView.text = "${state.day}.${state.month}.${state.year}"
            categories.forEach {
                it.isChecked = it.text == state.selectedCategory
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.sumInputEditText.addTextChangedListener(watcher)
    }

    override fun onPause() {
        super.onPause()
        binding.sumInputEditText.removeTextChangedListener(watcher)
    }

    override fun onDestroy() {
        super.onDestroy()
        materialDatePicker.clearOnPositiveButtonClickListeners()
        _binding = null
    }
}

abstract class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
}
