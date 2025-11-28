package com.example.finup.Transactions.list.presentation

import com.example.finup.core.LiveDataWrapper

interface TransactionsListLiveDataWrapper: LiveDataWrapper{

    interface UpdateList: LiveDataWrapper.Update<List<DisplayItemUi>>

    interface Read: LiveDataWrapper.Read<List<DisplayItemUi>>

    interface Mutable: UpdateList, Read

    class Base: LiveDataWrapper.Abstract<List<DisplayItemUi>>(), Mutable

}