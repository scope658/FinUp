package com.example.finup.Transactions.list.presentation

import com.example.finup.core.LiveDataWrapper

interface TransactionListUiStateWrapper: LiveDataWrapper {

    interface Update: LiveDataWrapper.Update<TransactionsListUiState>
    interface Read: LiveDataWrapper.Read<TransactionsListUiState>

    interface Mutable: Update, Read

    class Base: Mutable, LiveDataWrapper.Abstract<TransactionsListUiState>()
}