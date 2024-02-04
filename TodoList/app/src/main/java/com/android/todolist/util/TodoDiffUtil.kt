package com.android.todolist.util

import androidx.recyclerview.widget.DiffUtil
import com.android.todolist.ui.todo.TodoListItem

object TodoDiffUtil : DiffUtil.ItemCallback<TodoListItem>() {

    override fun areItemsTheSame(oldItem: TodoListItem, newItem: TodoListItem): Boolean =
        if (oldItem is TodoListItem.Item && newItem is TodoListItem.Item) {
            oldItem.id == newItem.id
        } else {
            oldItem == newItem
        }

    override fun areContentsTheSame(
        oldItem: TodoListItem,
        newItem: TodoListItem
    ): Boolean = oldItem == newItem

}