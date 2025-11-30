package com.example.finup.Transactions.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.espresso.contrib.RecyclerViewActions
import com.example.finup.createEdit.presentation.CreateEditTransactionScreen
import com.example.finup.Transactions.list.domain.StateManager
import com.example.finup.Transactions.list.domain.TransactionsListUseCase
import com.example.finup.Transactions.list.domain.NavigationMonthUseCase
import com.example.finup.core.presentation.DispatchersList
import com.example.finup.main.Navigation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class TransactionsListViewModel(
    private val transactionsListWrapper: TransactionsListLiveDataWrapper.Mutable,
    private val uiStateLiveDataWrapper: TransactionListUiStateWrapper.Mutable,
    private val transactionMapper: TransactionUiMapper,
    private val transactionsListUseCase: TransactionsListUseCase,
    private val navigationByMonthUseCase: NavigationMonthUseCase,
    private val navigation: Navigation.Update,
    private val stateManagerWrapper: StateManager.All,
    private val dispatchersList: DispatchersList,
) : ViewModel() {

    val mutex = Mutex()
    fun loadTransactions() {
        viewModelScope.launch(dispatchersList.io()) {
            mutex.withLock {
                val currentYearMonth = stateManagerWrapper.restoreYearMonth()
                val currentScreenType = stateManagerWrapper.restoreCurrentScreenType()
                val result = transactionsListUseCase(currentYearMonth, currentScreenType)
                withContext(dispatchersList.ui()) {
                    val transactionListUi = transactionMapper.toUiLayer(
                        result.listTransactions,
                        result.formattedDateYearMonth
                    )
                    transactionsListWrapper.update(transactionListUi)
                    uiStateLiveDataWrapper.update(
                        TransactionsListUiState(
                            screenType = currentScreenType,
                            title = result.formattedDateYearMonth,
                            total = result.totalSumByMonth,
                        )
                    )
                }
            }
        }
    }

    fun navigateMonth(forward: Boolean) {
        viewModelScope.launch(dispatchersList.io()) {
            val currentYearMonth = stateManagerWrapper.restoreYearMonth()
            val currentScreenType = stateManagerWrapper.restoreCurrentScreenType()
            val navigatedYearMonth = navigationByMonthUseCase(currentYearMonth, forward)
            stateManagerWrapper.saveYearMonthState(navigatedYearMonth.id)
            val result = transactionsListUseCase(navigatedYearMonth, currentScreenType)
            withContext(dispatchersList.ui()) {
                val transactionListUi = transactionMapper.toUiLayer(
                    result.listTransactions,
                    result.formattedDateYearMonth
                )
                transactionsListWrapper.update(transactionListUi)
                uiStateLiveDataWrapper.update(
                    TransactionsListUiState(
                        screenType = currentScreenType,
                        title = result.formattedDateYearMonth,
                        total = result.totalSumByMonth,
                    )
                )
            }
        }
    }

    fun editTransaction(transactionUi: DisplayItemUi.TransactionDetails) {

        navigation.update(
            CreateEditTransactionScreen(
                screenType = "Edit",
                transactionUi.id,
                transactionUi.type
            )
        )
    }

    fun saveScreenType(screenType: String) {
        viewModelScope.launch(dispatchersList.io()) {
            mutex.withLock {
                stateManagerWrapper.saveCurrentScreenType(screenType)
            }
        }
    }

    fun createTransaction() {
        viewModelScope.launch(dispatchersList.io()) {
            val screenType = stateManagerWrapper.restoreCurrentScreenType()
            withContext(dispatchersList.ui()) {
                navigation.update(CreateEditTransactionScreen("Create", 0L, screenType))
            }
        }
    }

    fun uiStateLiveData() = uiStateLiveDataWrapper.liveData()
    fun listLiveData() = transactionsListWrapper.liveData()
}