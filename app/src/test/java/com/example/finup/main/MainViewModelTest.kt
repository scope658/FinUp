package com.example.finup.main

import com.example.finup.Transactions.list.presentation.TransactionsListScreen
import com.example.finup.core.FakeNavigation
import com.example.finup.core.Order
import org.junit.Before
import org.junit.Test

class MainViewModelTest {
    lateinit var order: Order
    lateinit var navigation: FakeNavigation
    lateinit var viewModel: MainViewModel
    @Before
    fun setup() {
        order = Order()
        navigation = FakeNavigation.Base(order)
        viewModel = MainViewModel(
            navigation = navigation,
        )
    }

    @Test
    fun `init test`() {
        viewModel.init(firstRun = true)
        navigation.check(TransactionsListScreen)
    }
}
