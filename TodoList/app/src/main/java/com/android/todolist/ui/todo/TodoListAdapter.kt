package com.android.todolist.ui.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.todolist.util.TodoDiffUtil
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.ItemTodoListBinding

class TodoListAdapter(
    private val itemClickListener: (TodoModel) -> Unit,
    private val switchClickListener: (TodoModel) -> Unit
) : ListAdapter<TodoModel, TodoListAdapter.ViewHolder>(TodoDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, switchClickListener, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: ItemTodoListBinding,
        private val switchClickListener: ((TodoModel) -> Unit)?,
        private val itemClickListener: ((TodoModel) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoModel) = with(binding) {
            tvTodoTitle.text = item.title
            tvTodoContent.text = item.content
            switchTodo.isChecked = item.isBookmarked

            switchTodo.setOnClickListener {
                switchClickListener?.invoke(item)
            }

            itemLayout.setOnClickListener {
                itemClickListener?.invoke(item)
            }
        }
    }
}