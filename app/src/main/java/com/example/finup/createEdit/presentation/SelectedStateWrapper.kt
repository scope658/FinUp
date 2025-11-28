package com.example.finup.createEdit.presentation

import androidx.lifecycle.MutableLiveData
import com.example.finup.core.LiveDataWrapper

interface SelectedStateWrapper: LiveDataWrapper {

    interface Update: LiveDataWrapper.Update<SelectedStateUi>

    interface Read: LiveDataWrapper.Read<SelectedStateUi>

    interface UpdateCurrentSelectedState {
        fun updateSum(value: Int)

        fun updateSelectedDate(day: Int,month: Int,year: Int)

        fun updateSelectedCategory(category: String)
    }

    interface Mutable: Update, Read, UpdateCurrentSelectedState

    class Base: Mutable, LiveDataWrapper.Abstract<SelectedStateUi>(MutableLiveData()) {
        init {
            liveData.value = SelectedStateUi(
                selectedCategory = "",
                sum = 0,
                day = 0,
                year = 0,
                month = 0,
            )
        }
        override fun updateSum(value: Int) {
            liveData.value = liveData.value!!.copy(sum = value)
        }

        override fun updateSelectedDate(day: Int,month: Int,year: Int) {
            liveData.value = liveData.value!!.copy(day = day, month = month, year = year)
        }

        override fun updateSelectedCategory(category: String) {
            liveData.value = liveData.value!!.copy(selectedCategory = category)
        }
    }
}