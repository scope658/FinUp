package com.example.finup.Transactions.list.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.finup.R
import com.example.finup.databinding.HeaderForAdapterBinding
import com.example.finup.databinding.ItemForHolderBinding


class TransactionsListAdapter(
    private val onItemClick: (DisplayItemUi.TransactionDetails) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = ArrayList<DisplayItemUi>()

    override fun getItemViewType(position: Int): Int {
        return list[position].getItemType()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> HeaderViewHolder(HeaderForAdapterBinding.inflate(LayoutInflater.from(parent.context)))
            1 -> ItemViewHolder(ItemForHolderBinding.inflate(LayoutInflater.from(parent.context)),onItemClick)
            else -> throw IllegalStateException("view type not found")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(list[position] as DisplayItemUi.TransactionDate)
            is ItemViewHolder -> holder.bind(list[position] as DisplayItemUi.TransactionDetails)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItems(newList: List<DisplayItemUi>) {
        val diffUtil = DiffUtil(newList,list)
        val calculating = DiffUtil.calculateDiff(diffUtil)
        list.clear()
        list.addAll(newList)
        calculating.dispatchUpdatesTo(this)
    }
}


class HeaderViewHolder(private val binding: HeaderForAdapterBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(header: DisplayItemUi.TransactionDate) {
        binding.headerDateTextView.text = header.day
        binding.headerTotalSumTextView.text = header.totalSumByDay

    }
}

class ItemViewHolder(private val binding: ItemForHolderBinding,
    private val onItemClick: ((DisplayItemUi.TransactionDetails) -> Unit)
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(details: DisplayItemUi.TransactionDetails) {
        binding.itemNameTextView.text = details.name
        binding.itemSumTextView.text = details.sum.toString()
        val color = when(details.type) {
            "Expense" -> {
                ContextCompat.getColor(binding.root.context,R.color.Red)
            }
            "Income" -> {
                ContextCompat.getColor(binding.root.context,R.color.Green)
            }
            else -> { ContextCompat.getColor(binding.root.context, R.color.black) }
        }
        binding.itemSumTextView.setTextColor(color)
        binding.root.setOnClickListener {
            onItemClick(details)
        }
    }
}

class DiffUtil(
    private val currentList: List<DisplayItemUi>,
    private val oldList: List<DisplayItemUi>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return currentList.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
       return oldList[oldItemPosition] == currentList[newItemPosition]
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition] == currentList[newItemPosition]
    }

}
