package com.example.finup.Transactions.list.presentation


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.finup.R
import com.example.finup.core.ProvideViewModel
import com.example.finup.databinding.TransactionsListPageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionsListFragment : Fragment(R.layout.transactions_list_page) {

    private var _binding: TransactionsListPageBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = TransactionsListPageBinding.bind(view)
        val viewModel =
            (activity as ProvideViewModel).getViewModel(this, TransactionsListViewModel::class.java)
        startLoadTransactions(
            {},
            { viewModel.loadTransactions() })
        val adapter = TransactionsListAdapter {
            viewModel.editTransaction(it)
        }
        binding.recyclerView.adapter = adapter
        binding.rightImageViewId.setOnClickListener {
            viewModel.navigateMonth(true)
        }

        binding.leftImageViewId.setOnClickListener {
            viewModel.navigateMonth(false)
        }

        val expenseId = view.findViewById<View>(R.id.expenseIcon)
        expenseId

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.incomeIcon -> {
                    startLoadTransactions(
                        { viewModel.saveScreenType("Income") },
                        { viewModel.loadTransactions() })
                    true
                }

                R.id.expenseIcon -> {
                    startLoadTransactions(
                        { viewModel.saveScreenType("Expense") },
                        { viewModel.loadTransactions() })
                    true
                }

                else -> false
            }
        }

        binding.addFloatingButton.setOnClickListener {
            viewModel.createTransaction()
        }

        viewModel.uiStateLiveData().observe(viewLifecycleOwner) {

            when (it.screenType) {
                "Expense" -> {
                    binding.bottomNav.menu.findItem(R.id.expenseIcon).isChecked = true
                    binding.titleSumTextView.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.Red
                        )
                    )
                }

                "Income" -> {
                    binding.bottomNav.menu.findItem(R.id.incomeIcon).isChecked = true
                    binding.titleSumTextView.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.Green
                        )
                    )
                }

            }
            binding.titleMonthTextView.text = it.title
            binding.titleSumTextView.text = it.total
        }
        viewModel.listLiveData().observe(viewLifecycleOwner) {
            adapter.addItems(it)
        }
    }

    private fun startLoadTransactions(
        saveScreenType: suspend () -> Unit,
        loadTransactions: suspend () -> Unit,
    ) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            saveScreenType()
            loadTransactions()
        }
    }
}