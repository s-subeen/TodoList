package com.android.todolist.data

import androidx.annotation.StringRes
import com.android.todolist.R

data class TodoUiState(
    val list: List<TodoModel>,
) {
    companion object {
        fun init() = TodoUiState(
            list = emptyList()
        )
    }
}

data class TodoContentUiState(
    val title: String?,
    val content: String?
)

data class TodoContentButtonUiState(
    @StringRes val text: Int,
    val enabled: Boolean
) {
    companion object {
        fun init() = TodoContentButtonUiState(
            text = R.string.button_create,
            enabled = false
        )
    }
}

data class TodoErrorUiState(
    val title: TodoContentMessage,
    val content: TodoContentMessage
) {
    companion object {
        fun init() = TodoErrorUiState(
            title = TodoContentMessage.PASS,
            content = TodoContentMessage.PASS
        )
    }
}