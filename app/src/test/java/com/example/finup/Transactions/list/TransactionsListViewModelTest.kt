package com.example.finup.Transactions.list


import androidx.lifecycle.LiveData
import com.example.finup.createEdit.presentation.CreateEditTransactionScreen
import com.example.finup.Transactions.list.FakeTransactionMapper.Companion.TRANSACTIONS_MAPPER
import com.example.finup.Transactions.list.FakeTransactionsListLiveDataWrapper.Companion.TRANSACTION_UPDATE_LIST_LIVEDATA
import com.example.finup.Transactions.list.FakeUiStateLiveDataWrapper.Companion.UI_STATE_UPDATE_LIVEDATA
import com.example.finup.Transactions.list.FakeYearMonthStateManager.Companion.GET_YEAR_MONTH_MANAGER
import com.example.finup.Transactions.list.FakeYearMonthStateManager.Companion.RESTORE_SCREEN_TYPE_MANAGER
import com.example.finup.Transactions.list.FakeYearMonthStateManager.Companion.SAVE_SCREEN_TYPE_MANAGER
import com.example.finup.Transactions.list.FakeYearMonthStateManager.Companion.SAVE_YEAR_MONTH_MANAGER
import com.example.finup.Transactions.list.domain.TransactionsListUseCase
import com.example.finup.Transactions.list.domain.NavigationMonthUseCase
import com.example.finup.Transactions.list.presentation.DisplayItemUi
import com.example.finup.Transactions.list.presentation.TransactionsListLiveDataWrapper
import com.example.finup.Transactions.list.presentation.TransactionsListViewModel
import com.example.finup.core.FakeNavigation
import com.example.finup.core.Order
import com.example.finup.createEdit.domain.YearMonth
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.example.finup.Transactions.list.domain.Result
import com.example.finup.Transactions.list.domain.StateManager
import com.example.finup.Transactions.list.domain.Transaction
import com.example.finup.Transactions.list.presentation.TransactionsListUiState
import com.example.finup.Transactions.list.presentation.TransactionListUiStateWrapper
import com.example.finup.Transactions.list.presentation.TransactionUiMapper

@RunWith(RobolectricTestRunner::class)
class TransactionsListViewModelTest {

    private lateinit var order: Order
    private lateinit var viewModel: TransactionsListViewModel
    private lateinit var transactionsListWrapper: FakeTransactionsListLiveDataWrapper
    private lateinit var uiStateLiveDataWrapper: FakeUiStateLiveDataWrapper
    private lateinit var transactionMapper: FakeTransactionMapper
    private lateinit var transactionsListUseCase: FakeTransactionsListUseCase
    private lateinit var navigation: FakeNavigation
    private lateinit var navigationByMonthUseCase: FakeNavigationByMonthUseCase
    private lateinit var stateManagerWrapper: FakeYearMonthStateManager


    @Before
    fun setUp() {
        order = Order()
        transactionsListWrapper = FakeTransactionsListLiveDataWrapper.Base(order)
        uiStateLiveDataWrapper = FakeUiStateLiveDataWrapper.Base(order)
        transactionMapper = FakeTransactionMapper.Base(order)
        transactionsListUseCase = FakeTransactionsListUseCase.Base(order)
        navigationByMonthUseCase = FakeNavigationByMonthUseCase.Base(order)
        navigation = FakeNavigation.Base(order)
        stateManagerWrapper = FakeYearMonthStateManager.Base(order)
        viewModel = TransactionsListViewModel(
            transactionsListWrapper = transactionsListWrapper,
            uiStateLiveDataWrapper = uiStateLiveDataWrapper,
            transactionMapper = transactionMapper,
            transactionsListUseCase = transactionsListUseCase,
            navigationByMonthUseCase = navigationByMonthUseCase,
            navigation = navigation,
            stateManagerWrapper = stateManagerWrapper,
            dispatcher = Dispatchers.Unconfined,
            dispatcherMain = Dispatchers.Unconfined,
        )
    }

    fun mock() {
        stateManagerWrapper.expectedSavedYearMonth(expectedSavedYearMonth)
        stateManagerWrapper.expectedScreenType("Expense")
        transactionsListUseCase.expectedResult(expectedUseCaseResult)
        transactionMapper.expectedUiLayer(expectedUiValue)
    }

    @Test
    fun `load transactions test`() {
        mock()
        viewModel.loadTransactions()
        stateManagerWrapper.checkRestoreYearMonthCalled(1)
        stateManagerWrapper.checkRestoreScreenTypeCalled(1)
        transactionsListUseCase.checkCalledTimes(
            expectedYearMonth = expectedSavedYearMonth,
            "Expense"
        )
        transactionMapper.checkCalledTime(1)
        transactionsListWrapper.check(expectedUiValue)
        uiStateLiveDataWrapper.check(TransactionsListUiState("Expense", "September 2025", "6000"))
        order.check(
            listOf(
                GET_YEAR_MONTH_MANAGER,
                RESTORE_SCREEN_TYPE_MANAGER,
                GET_TRANSACTIONS_LIST_USE_CASE,
                TRANSACTIONS_MAPPER,
                TRANSACTION_UPDATE_LIST_LIVEDATA,
                UI_STATE_UPDATE_LIVEDATA
            )
        )
    }

    @Test
    fun `month navigation for toolBar`() {
        mock()
        val expectedForwardYearMonth = YearMonth(2,10,2025)
        navigationByMonthUseCase.expectedYearMonth(expectedForwardYearMonth)
        viewModel.navigateMonth(forward = true)
        stateManagerWrapper.checkRestoreYearMonthCalled(1)
        stateManagerWrapper.checkRestoreScreenTypeCalled(1)
        navigationByMonthUseCase.check(true, expectedCurrentYearMonth = expectedSavedYearMonth)
        stateManagerWrapper.checkSaveYearMonthIsCalled(2L)
        transactionsListUseCase.checkCalledTimes(expectedYearMonth = expectedForwardYearMonth,"Expense")
        transactionMapper.checkCalledTime(1)
        transactionsListWrapper.check(expectedUiValue)
        uiStateLiveDataWrapper.check(
            TransactionsListUiState(
                screenType = "Expense",
                title = "September 2025",
                total = "6000"
            )
        )
        order.check(
            listOf(
                GET_YEAR_MONTH_MANAGER,
                RESTORE_SCREEN_TYPE_MANAGER,
                NAVIGATE_BY_MONTH_USE_CASE,
                SAVE_YEAR_MONTH_MANAGER,
                GET_TRANSACTIONS_LIST_USE_CASE,
                TRANSACTIONS_MAPPER,
                TRANSACTION_UPDATE_LIST_LIVEDATA,
                UI_STATE_UPDATE_LIVEDATA
            )
        )
    }


    @Test
    fun `navigating to edit transaction page`() {
        viewModel.editTransaction(
            transactionUi = DisplayItemUi.TransactionDetails(
                id = 2L,
                sum = 2000,
                type = "Income",
                name = "Other",
                dateId = 10L
            )
        )
        navigation.check(CreateEditTransactionScreen(screenType = "Edit", 2L, "Income"))
    }

    @Test
    fun `save screen type`() {
        viewModel.saveScreenType("Income")
        stateManagerWrapper.checkSaveScreenTypeIsCalled("Income")
    }

}


private interface FakeUiStateLiveDataWrapper : TransactionListUiStateWrapper.Mutable {

    fun check(expectedUiState: TransactionsListUiState)

    companion object {
        const val UI_STATE_UPDATE_LIVEDATA = "UiStateLiveDataWrapper#Update"
    }

    class Base(private val order: Order) : FakeUiStateLiveDataWrapper {

        lateinit var actualUiState: TransactionsListUiState

        override fun update(value: TransactionsListUiState) {
            actualUiState = value
            order.add(UI_STATE_UPDATE_LIVEDATA)
        }

        override fun liveData(): LiveData<TransactionsListUiState> {
            throw IllegalStateException("not used in test")
        }

        override fun check(expectedUiState: TransactionsListUiState) {
            TestCase.assertEquals(expectedUiState, actualUiState)
        }
    }
}


private const val GET_TRANSACTIONS_LIST_USE_CASE = "GetTransactionsListByPeriodUseCase#invoke"

private interface FakeTransactionsListUseCase : TransactionsListUseCase {

    fun checkCalledTimes(expectedYearMonth: YearMonth, expectedType: String)

    fun expectedResult(result: Result)

    class Base(private val order: Order) : FakeTransactionsListUseCase {

        private lateinit var expectedResult: com.example.finup.Transactions.list.domain.Result
        private lateinit var actualYearMonth: YearMonth
        private lateinit var actualType: String

        override suspend fun invoke(yearMonth: YearMonth, type: String): Result {
            order.add(GET_TRANSACTIONS_LIST_USE_CASE)
            actualYearMonth = yearMonth
            actualType = type
            return expectedResult
        }

        override fun checkCalledTimes(expectedYearMonth: YearMonth, expectedType: String) {
            assertEquals(expectedYearMonth, actualYearMonth)
            assertEquals(expectedType, actualType)
        }

        override fun expectedResult(result: Result) {
            expectedResult = result
        }
    }
}

private interface FakeTransactionsListLiveDataWrapper : TransactionsListLiveDataWrapper.Mutable {

    fun check(expected: List<DisplayItemUi>)

    companion object {
        const val TRANSACTION_UPDATE_LIST_LIVEDATA = "TransactionsListLiveDataWrapper#Update"
    }

    class Base(private val order: Order) : FakeTransactionsListLiveDataWrapper {

        private lateinit var actualList: List<DisplayItemUi>

        override fun update(value: List<DisplayItemUi>) {
            actualList = value
            order.add(TRANSACTION_UPDATE_LIST_LIVEDATA)
        }

        override fun check(expected: List<DisplayItemUi>) {
            assertEquals(expected, actualList)
        }

        override fun liveData(): LiveData<List<DisplayItemUi>> {
            throw IllegalStateException("not used in test")
        }
    }
}


private interface FakeTransactionMapper : TransactionUiMapper {


    fun checkCalledTime(expectedCalledTimes: Int)

    fun expectedUiLayer(expectedDisplayItem: List<DisplayItemUi>)

    companion object {
        const val TRANSACTIONS_MAPPER = "TransactionMappers#ToUiLayer"
    }

    class Base(private val order: Order) : FakeTransactionMapper {

        private lateinit var mock: List<DisplayItemUi>
        private var actualCalledTimes: Int = 0
        override fun toUiLayer(
            transactions: List<Transaction>,
            month: String
        ): List<DisplayItemUi> {
            actualCalledTimes++
            order.add(TRANSACTIONS_MAPPER)
            return mock
        }

        override fun expectedUiLayer(expectedDisplayItem: List<DisplayItemUi>) {
            mock = expectedDisplayItem
        }

        override fun checkCalledTime(expectedCalledTimes: Int) {
            assertEquals(expectedCalledTimes, actualCalledTimes)
        }
    }
}

private const val NAVIGATE_BY_MONTH_USE_CASE = "NavigationMonthUseCase#invoke"

private interface FakeNavigationByMonthUseCase : NavigationMonthUseCase {

    fun expectedYearMonth(mock: YearMonth)
    fun check(expectedForward: Boolean, expectedCurrentYearMonth: YearMonth)

    class Base(private val order: Order) : FakeNavigationByMonthUseCase {

        private lateinit var mockYearMonth: YearMonth
        private var actualForward: Boolean? = null

        private lateinit var actualCurrentMonth: YearMonth

        override suspend fun invoke(

            currentMonth: YearMonth,
            forward: Boolean
        ): YearMonth {
            actualForward = forward
            actualCurrentMonth = currentMonth
            order.add(NAVIGATE_BY_MONTH_USE_CASE)
            return mockYearMonth
        }

        override fun expectedYearMonth(mock: YearMonth) {
            mockYearMonth = mock
        }

        override fun check(expectedForward: Boolean, expectedCurrentYearMonth: YearMonth) {
            assertEquals(expectedForward, actualForward)
            assertEquals(expectedCurrentYearMonth, actualCurrentMonth)
        }

    }
}

private interface FakeYearMonthStateManager : StateManager.All {

    fun checkRestoreYearMonthCalled(expectedCalledTimes: Int)

    fun checkRestoreScreenTypeCalled(expectedCalledTimes: Int)

    fun checkSaveYearMonthIsCalled(expectedYearMonthId: Long)

    fun expectedSavedYearMonth(expectedYearMonth: YearMonth)

    fun expectedScreenType(expectedScreenType: String)

    fun checkSaveScreenTypeIsCalled(expectedScreenType: String)

    companion object {
        const val GET_YEAR_MONTH_MANAGER = "YearMonthStateManager#getInitialYearMonth"
        const val SAVE_YEAR_MONTH_MANAGER = "YearMonthStateManager#saveYearMonthState"
        const val RESTORE_SCREEN_TYPE_MANAGER = "YearMonthStateManager#restoreCurrentScreenType"
        const val SAVE_SCREEN_TYPE_MANAGER = "FakeYearMonthStateManager#saveCurrentScreenType"
    }

    class Base(private val order: Order) : FakeYearMonthStateManager {

        private var restoreYearMonthCalledTimes: Int = 0
        private var restoreScreenTypeCalledTimes: Int = 0
        private lateinit var mockYearMonth: YearMonth
        private lateinit var mockScreenType: String
        private var savedYearMonthId = 0L
        private lateinit var savedScreenType: String

        override suspend fun restoreYearMonth(): YearMonth {
            restoreYearMonthCalledTimes++
            order.add(GET_YEAR_MONTH_MANAGER)
            return mockYearMonth
        }

        override suspend fun saveYearMonthState(id: Long) {
            order.add(SAVE_YEAR_MONTH_MANAGER)
            savedYearMonthId = id
        }

        override fun expectedSavedYearMonth(yearMonth: YearMonth) {
            mockYearMonth = yearMonth
        }

        override fun expectedScreenType(expectedScreenType: String) {
            mockScreenType = expectedScreenType
        }

        override fun checkSaveScreenTypeIsCalled(expectedScreenType: String) {
            assertEquals(expectedScreenType, savedScreenType)
        }


        override suspend fun restoreCurrentScreenType(): String {
            restoreScreenTypeCalledTimes++
            order.add(RESTORE_SCREEN_TYPE_MANAGER)
            return mockScreenType
        }

        override suspend fun saveCurrentScreenType(screenType: String) {
            order.add(SAVE_SCREEN_TYPE_MANAGER)
            savedScreenType = screenType
        }


        override fun checkRestoreYearMonthCalled(expectedCalledTimes: Int) {
            assertEquals(expectedCalledTimes, restoreYearMonthCalledTimes)
        }

        override fun checkRestoreScreenTypeCalled(expectedCalledTimes: Int) {
            assertEquals(expectedCalledTimes, restoreScreenTypeCalledTimes)
        }

        override fun checkSaveYearMonthIsCalled(expectedYearMonthId: Long) {
            assertEquals(expectedYearMonthId, savedYearMonthId)
        }
    }
}

private val expectedSavedYearMonth = YearMonth(
    id = 1L,
    month = 9,
    year = 2025
)
private val expectedUseCaseResult = Result(
    listOf(
        Transaction(
            id = 5L,
            name = "fakeName",
            sum = 2000,
            type = "Expense",
            day = 24,
            dateId = 1L
        ),
        Transaction(
            id = 6L,
            name = "fakeName",
            sum = 4000,
            type = "Expense",
            day = 25,
            dateId = 1L
        ),
    ),
    formattedDateYearMonth = "September 2025",
    totalSumByMonth = "6000",
)
private val expectedUiValue = listOf(
    DisplayItemUi.TransactionDate(day = "24 September", "2000"),
    DisplayItemUi.TransactionDetails(
        id = 5L,
        sum = 2000,
        name = "fakeCategory",
        type = "Expense",
        dateId = 1L
    ),
    DisplayItemUi.TransactionDate(day = "25 September", "6000"),
    DisplayItemUi.TransactionDetails(
        id = 6L,
        sum = 4000,
        name = "fakeCategory",
        type = "Expense",
        dateId = 1L
    ),
)