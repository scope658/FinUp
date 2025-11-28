package com.example.finup.main

import androidx.lifecycle.ViewModel
import com.example.finup.Transactions.list.presentation.TransactionsListScreen


class MainViewModel(private val navigation: Navigation.Mutable,
) : ViewModel() {

    fun init(firstRun: Boolean) {
        if(firstRun)
            navigation.update(TransactionsListScreen)
    }

    fun navigationLiveData() = navigation.liveData()
}