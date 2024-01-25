package com.android.todolist

import com.android.todolist.data.TodoModel

interface OnItemClickListener {
    fun onClickSwitch(todoModel: TodoModel)
    fun onClickItem(todoModel: TodoModel)
}