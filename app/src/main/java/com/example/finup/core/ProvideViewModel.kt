package com.example.finup.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.finup.Transactions.createEdit.CreateEditTransactionViewModel
import com.example.finup.Transactions.createEdit.CreateEditUiStateWrapper
import com.example.finup.Transactions.createEdit.SelectedStateWrapper
import com.example.finup.Transactions.list.TransactionListUiStateWrapper
import com.example.finup.Transactions.list.TransactionsListLiveDataWrapper
import com.example.finup.Transactions.list.TransactionsListViewModel
import com.example.finup.Transactions.mappers.TransactionUiMapper
import com.example.finup.data.dataStore.DataStoreManagerImpl
import com.example.finup.data.db.dao.TransactionDao
import com.example.finup.data.db.dao.YearMonthDao
import com.example.finup.data.repositories.Now
import com.example.finup.data.repositories.SettingsStateRepositoryImpl
import com.example.finup.data.repositories.TransactionRepositoryImpl
import com.example.finup.data.repositories.YearMonthRepositoryImpl
import com.example.finup.domain.FakeDateProviderImpl
import com.example.finup.domain.DateProviderImpl
import com.example.finup.domain.StateManager
import com.example.finup.domain.useCases.GetOrCreatePeriodUseCase
import com.example.finup.domain.useCases.GetTransactionsListByPeriodUseCase
import com.example.finup.domain.useCases.NavigationMonthUseCase
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
        private val getTransactionsListByPeriodUseCase =
            GetTransactionsListByPeriodUseCase.Base(transactionRepository, dateProvider)
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
                    getTransactionsListByPeriodUseCase,
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