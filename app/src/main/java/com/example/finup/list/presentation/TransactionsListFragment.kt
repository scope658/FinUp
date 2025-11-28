package com.example.finup.list.presentation


import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.finup.R
import com.example.finup.core.ProvideViewModel
import com.example.finup.databinding.TransactionsListPageBinding

class TransactionsListFragment : Fragment(R.layout.transactions_list_page) {

    private var _binding: TransactionsListPageBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _binding = TransactionsListPageBinding.bind(view)
        val viewModel =
            (activity as ProvideViewModel).getViewModel(this, TransactionsListViewModel::class.java)

        viewModel.init()

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
        val incomeId = R.id.incomeIcon
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.incomeIcon -> {
                    viewModel.navigateToIncomes()
                    true
                }

                R.id.expenseIcon -> {
                    viewModel.navigateToExpenses()
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
}