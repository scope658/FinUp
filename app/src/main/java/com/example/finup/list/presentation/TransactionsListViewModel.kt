package com.example.finup.list.presentation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finup.createEdit.presentation.CreateEditTransactionScreen
import com.example.finup.Transactions.list.domain.StateManager
import com.example.finup.Transactions.list.domain.GetTransactionsListByPeriodUseCase
import com.example.finup.Transactions.list.domain.NavigationMonthUseCase
import com.example.finup.main.Navigation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionsListViewModel(
    private val transactionsListWrapper: TransactionsListLiveDataWrapper.Mutable,
    private val uiStateLiveDataWrapper: TransactionListUiStateWrapper.Mutable,
    private val transactionMapper: TransactionUiMapper.ToUiLayer,
    private val getTransactionsListByPeriodUseCase: GetTransactionsListByPeriodUseCase,
    private val navigationByMonthUseCase: NavigationMonthUseCase,
    private val navigation: Navigation.Update,
    private val stateManagerWrapper: StateManager.All,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {

    companion object {

        @VisibleForTesting
        var loadDataCalledTimes = 0
    }

    fun init() {
        viewModelScope.launch(dispatcher) {
            loadData()
        }
    }

    suspend fun loadData() {
        val currentYearMonth = stateManagerWrapper.restoreYearMonth()
        val currentScreenType = stateManagerWrapper.restoreCurrentScreenType()
        val result = getTransactionsListByPeriodUseCase(currentYearMonth, currentScreenType)
        withContext(dispatcherMain) {
            val transactionListUi = transactionMapper.toUiLayer(
                result.listTransactions,
                result.formattedDateYearMonth
            )
            transactionsListWrapper.update(transactionListUi)
            uiStateLiveDataWrapper.update(
                ShowDateTitle(
                    screenType = currentScreenType,
                    title = result.formattedDateYearMonth,
                    total = result.totalSumByMonth,
                )
            )
        }
        loadDataCalledTimes++
    }

    fun navigateMonth(forward: Boolean) {

        viewModelScope.launch(dispatcher) {

            val currentYearMonth = stateManagerWrapper.restoreYearMonth()
            val currentScreenType = stateManagerWrapper.restoreCurrentScreenType()
            val navigatedYearMonth = navigationByMonthUseCase(currentYearMonth, forward)
            stateManagerWrapper.saveYearMonthState(navigatedYearMonth.id)
            val result = getTransactionsListByPeriodUseCase(navigatedYearMonth, currentScreenType)
            withContext(dispatcherMain) {
                val transactionListUi = transactionMapper.toUiLayer(
                    result.listTransactions,
                    result.formattedDateYearMonth
                )
                transactionsListWrapper.update(transactionListUi)
                uiStateLiveDataWrapper.update(
                    ShowDateTitle(
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

    fun navigateToIncomes() {
        viewModelScope.launch(dispatcher) {
            stateManagerWrapper.saveCurrentScreenType("Income")
            loadData()
        }
    }

    fun navigateToExpenses() {
        viewModelScope.launch(dispatcher) {
            stateManagerWrapper.saveCurrentScreenType("Expense")
            loadData()
        }
    }

    fun createTransaction() {
        viewModelScope.launch(dispatcher) {
            val screenType = stateManagerWrapper.restoreCurrentScreenType()
            withContext(dispatcherMain) {
                navigation.update(CreateEditTransactionScreen("Create", 0L, screenType))
            }
        }
    }

    fun uiStateLiveData() = uiStateLiveDataWrapper.liveData()
    fun listLiveData() = transactionsListWrapper.liveData()
}