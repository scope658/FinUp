package com.example.finup.createEdit.presentation

data class SelectedStateUi(
    val selectedCategory: String,
    val sum: Int,
    val day: Int,
    val month: Int,
    val year: Int,
) {
    fun checkIsValid(): Boolean {
        return selectedCategory != "" && year != 0 && sum != 0
    }
}
