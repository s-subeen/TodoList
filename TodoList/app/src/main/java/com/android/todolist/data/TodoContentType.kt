package com.android.todolist.data

enum class TodoContentType {
    CREATE,
    UPDATE,
    DELETE
    ;
    companion object {
        fun getEntryType(
            ordinal: Int?
        ): TodoContentType {
            return TodoContentType.values().firstOrNull {
                it.ordinal == ordinal
            } ?: CREATE
        }
    }
}