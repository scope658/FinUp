package com.example.finup.createEdit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finup.Transactions.list.presentation.TransactionsListScreen
import com.example.finup.createEdit.domain.DateProvider
import com.example.finup.Transactions.list.domain.TransactionRepository
import com.example.finup.core.presentation.DispatchersList
import com.example.finup.createEdit.domain.GetOrCreatePeriodUseCase
import com.example.finup.main.Navigation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateEditTransactionViewModel(
    private val uiStateLiveDataWrapper: CreateEditUiStateWrapper.Mutable,
    private val repository: TransactionRepository.EditAndCreate,
    private val getOrCreatePeriodUseCase: GetOrCreatePeriodUseCase,
    private val navigation: Navigation.Update,
    private val selectedStateLiveDataWrapper: SelectedStateWrapper.Mutable,
    private val dateProvider: DateProvider.FormatLongToDateComponents,
    private val dispatchersList: DispatchersList,
) : ViewModel() {


    fun createInit(title: String) {
        uiStateLiveDataWrapper.update(CreateEditUiState.ShowCreateTransactionPage(title))
    }

    fun editInit(title: String, transactionId: Long, transactionType: String) {
        viewModelScope.launch(dispatchersList.io()) {
            val currentTransaction = repository.getOneTransaction(transactionId, transactionType)
            val currentYearMonth = getOrCreatePeriodUseCase.getById(currentTransaction.dateId)
            withContext(dispatchersList.ui()) {
                selectedStateLiveDataWrapper.update(
                    SelectedStateUi(
                        currentTransaction.name,
                        currentTransaction.sum,
                        currentTransaction.day,
                        currentYearMonth.month,
                        currentYearMonth.year,
                    )
                )
                uiStateLiveDataWrapper.update(
                    CreateEditUiState.ShowEditTransactionPage(
                        title,
                        currentTransaction.sum.toString(),
                    )
                )
            }
        }
    }

    fun edit(
        transactionId: Long,
        transactionType: String
    ) {

        val currentSelectedTypes = stateLiveData().value!!

        viewModelScope.launch(dispatchersList.io()) {

            val currentYearMonth = getOrCreatePeriodUseCase(
                currentSelectedTypes.year,
                currentSelectedTypes.month,
            )
            repository.editTransaction(
                transactionId,
                currentSelectedTypes.sum,
                currentSelectedTypes.selectedCategory,
                transactionType,
                currentSelectedTypes.day,
                currentYearMonth.id
            )
            withContext(dispatchersList.ui()) {
                navigation.update(TransactionsListScreen)
            }
        }
    }

    fun create(transactionType: String) {
        val selectedInput = stateLiveData().value!!
        viewModelScope.launch(dispatchersList.io()) {

            val yearMonth = getOrCreatePeriodUseCase(selectedInput.year, selectedInput.month)
            repository.createTransaction(
                selectedInput.sum,
                selectedInput.selectedCategory,
                transactionType,
                selectedInput.day,
                yearMonth.id
            )
            withContext(dispatchersList.ui()) {
                navigation.update(TransactionsListScreen)
            }
        }
    }

    fun delete(transactionId: Long) {
        viewModelScope.launch(dispatchersList.io()) {
            repository.deleteTransaction(transactionId)
        }
        navigation.update(TransactionsListScreen)
    }

    fun selectCategory(category: String) {
        selectedStateLiveDataWrapper.updateSelectedCategory(category)
    }

    fun selectDate(date: Long) {
        val (day, month, year) = dateProvider.formatLongToDateComponents(date)
        selectedStateLiveDataWrapper.updateSelectedDate(day, month, year)
    }

    fun updateSum(sum: Int) {
        selectedStateLiveDataWrapper.updateSum(sum)
    }

    fun comeback() {
        navigation.update(TransactionsListScreen)
    }

    override fun onCleared() {
        selectedStateLiveDataWrapper.update(SelectedStateUi("", 0, 0, 0, 0))
    }

    fun uiStateLiveData() = uiStateLiveDataWrapper.liveData()
    fun stateLiveData() = selectedStateLiveDataWrapper.liveData()
}