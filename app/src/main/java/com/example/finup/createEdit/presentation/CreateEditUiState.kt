package com.example.finup.createEdit.presentation

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

interface CreateEditUiState {

    fun show(
        buttons: List<MaterialButton>,
        titleTextView: TextView,
        deleteButton: Button,
        inputEditText: TextInputEditText,
    )

    data class ShowCreateTransactionPage(
        val title: String,
    ) : CreateEditUiState {
        override fun show(
            buttons: List<MaterialButton>,
            titleTextView: TextView,
            deleteButton: Button,
            inputEditText: TextInputEditText,
        ) {
            titleTextView.text = title
            buttons.forEach {
                it.visibility = View.VISIBLE
            }
            deleteButton.visibility = View.GONE
        }
    }

    data class ShowEditTransactionPage(
        val title: String,
        val sum: String,
    ) : CreateEditUiState {
        override fun show(
            buttons: List<MaterialButton>,
            titleTextView: TextView,
            deleteButton: Button,
            inputEditText: TextInputEditText,
        ) {
            inputEditText.setText(sum)
            titleTextView.text = title
            buttons.forEach {
                it.visibility = View.VISIBLE
            }
            deleteButton.visibility = View.VISIBLE
        }
    }
}