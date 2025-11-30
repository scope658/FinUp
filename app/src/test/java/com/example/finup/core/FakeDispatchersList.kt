package com.example.finup.core

import com.example.finup.core.presentation.DispatchersList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class FakeDispatchersList: DispatchersList {

    override fun io(): CoroutineDispatcher = Dispatchers.Unconfined
    override fun ui(): CoroutineDispatcher = Dispatchers.Unconfined

}