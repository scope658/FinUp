package com.example.finup.list.presentation

import com.example.finup.core.LiveDataWrapper

interface TransactionListUiStateWrapper: LiveDataWrapper {

    interface Update: LiveDataWrapper.Update<ShowDateTitle>
    interface Read: LiveDataWrapper.Read<ShowDateTitle>

    interface Mutable: Update, Read

    class Base: Mutable, LiveDataWrapper.Abstract<ShowDateTitle>()
}