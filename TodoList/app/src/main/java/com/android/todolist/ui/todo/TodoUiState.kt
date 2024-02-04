package com.android.todolist.ui.todo

data class TodoUiState(
    val list: List<TodoListItem>,
) {
    companion object {
        fun init() = TodoUiState(
            list = emptyList()
        )
    }
}
