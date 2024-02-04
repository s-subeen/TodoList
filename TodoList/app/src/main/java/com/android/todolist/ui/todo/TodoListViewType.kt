package com.android.todolist.ui.todo

enum class TodoListViewType {
    ITEM,
    UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): TodoListViewType = TodoListViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}