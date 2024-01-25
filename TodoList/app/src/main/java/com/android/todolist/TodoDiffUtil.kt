package com.android.todolist

import androidx.recyclerview.widget.DiffUtil
import com.android.todolist.data.TodoModel

object TodoDiffUtil : DiffUtil.ItemCallback<TodoModel>() {

    override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
        return oldItem == newItem
    }
}