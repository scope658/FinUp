package com.example.finup.createEdit.presentation

import com.example.finup.main.Screen

data class CreateEditTransactionScreen(
    val screenType: String,
    val transactionId: Long,
    val transactionType: String,
): Screen.Replace(CreateEditFragment.newInstance(screenType,transactionId,transactionType))
