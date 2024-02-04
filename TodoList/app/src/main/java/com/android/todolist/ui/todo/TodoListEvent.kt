package com.android.todolist.ui.todo

import com.android.todolist.data.TodoModel

sealed interface TodoListEvent {

    data class OpenContent(
        val position: Int,
        val item: TodoModel
    ) : TodoListEvent

}