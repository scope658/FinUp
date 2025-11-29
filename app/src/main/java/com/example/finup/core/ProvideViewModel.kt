package com.example.finup.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.finup.createEdit.presentation.CreateEditTransactionViewModel
import com.example.finup.createEdit.presentation.CreateEditUiStateWrapper
import com.example.finup.createEdit.presentation.SelectedStateWrapper
import com.example.finup.Transactions.list.presentation.TransactionListUiStateWrapper
import com.example.finup.Transactions.list.presentation.TransactionsListLiveDataWrapper
import com.example.finup.Transactions.list.presentation.TransactionsListViewModel
import com.example.finup.Transactions.list.presentation.TransactionUiMapper
import com.example.finup.Transactions.list.data.DataStoreManagerImpl
import com.example.finup.Transactions.list.data.db.dao.TransactionDao
import com.example.finup.Transactions.list.data.db.dao.YearMonthDao
import com.example.finup.core.data.Now
import com.example.finup.Transactions.list.data.SettingsStateRepositoryImpl
import com.example.finup.Transactions.list.data.TransactionRepositoryImpl
import com.example.finup.createEdit.data.YearMonthRepositoryImpl
import com.example.finup.createEdit.domain.FakeDateProviderImpl
import com.example.finup.createEdit.domain.DateProviderImpl
import com.example.finup.Transactions.list.domain.StateManager
import com.example.finup.createEdit.domain.GetOrCreatePeriodUseCase
import com.example.finup.Transactions.list.domain.TransactionsListUseCase
import com.example.finup.Transactions.list.domain.NavigationMonthUseCase
import com.example.finup.main.MainViewModel
import com.example.finup.main.Navigation


interface ProvideViewModel {


    fun <T : ViewModel> getViewModel(owner: ViewModelStoreOwner, modelClass: Class<T>): T

    class Base(transactionDao: TransactionDao, yearMonthDao: YearMonthDao, now: Now,dataStoreManager: DataStoreManagerImpl) :
        ViewModelProvider.Factory, ProvideViewModel {
        private val createEditUiStateWrapper = CreateEditUiStateWrapper.Base()
        private val transactionListWrapper = TransactionsListLiveDataWrapper.Base()
        private val transactionListUiStateWrapper = TransactionListUiStateWrapper.Base()
        private val transactionMapper = TransactionUiMapper.Base()
        private val dateProvider = DateProviderImpl()
        private val fakeDateProvider = FakeDateProviderImpl() //for Ui Tests, Please use that if you want to run Ui Tests
        private val transactionRepository = TransactionRepositoryImpl(transactionDao, now)
        private val yearMonthRepository = YearMonthRepositoryImpl(yearMonthDao, now)
        private val navigationMonthUseCase = NavigationMonthUseCase.Base(yearMonthRepository)
        private val transactionsListUseCase =
            TransactionsListUseCase.Base(transactionRepository, dateProvider)
        private val getOrCreatePeriodUseCase = GetOrCreatePeriodUseCase.Base(yearMonthRepository)
        private val navigation = Navigation.Base()
        private val yearMonthStateRepository = SettingsStateRepositoryImpl(dataStoreManager)
        private val stateManager = StateManager.Base(yearMonthRepository,  yearMonthStateRepository,fakeDateProvider)
        private val stateLiveDataWrapper = SelectedStateWrapper.Base()
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return when (modelClass) {
                MainViewModel::class.java -> MainViewModel(navigation)
                TransactionsListViewModel::class.java -> TransactionsListViewModel(
                    transactionListWrapper,
                    transactionListUiStateWrapper,
                    transactionMapper,
                    transactionsListUseCase,
                    navigationMonthUseCase,
                    navigation,
                    stateManager
                )

                CreateEditTransactionViewModel::class.java -> CreateEditTransactionViewModel(
                    createEditUiStateWrapper,
                    transactionRepository,
                    getOrCreatePeriodUseCase,
                    navigation,
                    stateLiveDataWrapper,
                    dateProvider,
                )

                else -> throw IllegalStateException("view Model doesnt exist, please watch provideVIewModel")
            } as T
        }

        override fun <T : ViewModel> getViewModel(
            owner: ViewModelStoreOwner,
            modelClass: Class<T>
        ): T {

            return ViewModelProvider(owner, this)[modelClass]
        }
    }
}