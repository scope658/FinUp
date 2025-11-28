package com.example.finup

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.example.finup.core.Core
import com.example.finup.core.ProvideViewModel
import com.example.finup.Transactions.list.data.DataStoreManagerImpl
import com.example.finup.core.data.Now

class App : Application(), ProvideViewModel {
    lateinit var factory: ProvideViewModel
    lateinit var core: Core
    lateinit var now: Now
    lateinit var dataStoreManager: DataStoreManagerImpl
    override fun onCreate() {
        super.onCreate()
        core = Core(applicationContext)
        now = Now.Base()
        dataStoreManager = DataStoreManagerImpl(applicationContext)
        factory = ProvideViewModel.Base(core.transactionDao(), core.yearMonthDao(), now,dataStoreManager)
    }

    override fun <T : ViewModel> getViewModel(owner: ViewModelStoreOwner, modelClass: Class<T>): T {
        return factory.getViewModel(owner, modelClass)
    }
}