package com.android.todolist.data

import android.content.Intent
import android.view.View
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

data class ContentButtonUiState(
    @StringRes val text: Int,
    val enabled: Boolean
) {
    companion object {
        fun init() = ContentButtonUiState(
            text = R.string.button_create,
            enabled = false
        )
    }
}

data class TodoButtonUiState(
    val buttonUiState: ContentButtonUiState,
    val deleteButtonVisibility: Int
) {
    companion object {
        fun init() = TodoButtonUiState(
            buttonUiState = ContentButtonUiState.init(),
            deleteButtonVisibility = View.GONE
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

sealed class TodoIntent {
    data class RegularIntent(val intent: Intent) : TodoIntent()
    data class DeleteIntent(val intent: Intent) : TodoIntent()
}