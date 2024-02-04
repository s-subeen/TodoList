package com.android.todolist.ui.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.todolist.util.TodoDiffUtil
import com.android.todolist.databinding.ItemTodoListBinding
import com.android.todolist.databinding.ItemUnknownBinding

class TodoListAdapter(
    private val onClickItem: (Int, TodoListItem) -> Unit,
    private val onBookmarkChecked: (Int, TodoListItem) -> Unit
) : ListAdapter<TodoListItem, TodoListAdapter.TodoViewHolder>(TodoDiffUtil) {

    abstract class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: TodoListItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is TodoListItem.Item -> TodoListViewType.ITEM
        else -> TodoListViewType.UNKNOWN
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder =
        when (TodoListViewType.from(viewType)) {
            TodoListViewType.ITEM -> TodoItemViewHolder(
                ItemTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickItem,
                onBookmarkChecked
            )

            else -> TodoUnknownViewHolder(
                ItemUnknownBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            )
        }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class TodoItemViewHolder(
        private val binding: ItemTodoListBinding,
        private val onClickItem: (Int, TodoListItem) -> Unit,
        private val onBookmarkChecked: (Int, TodoListItem) -> Unit
    ) : TodoViewHolder(binding.root) {
        override fun onBind(item: TodoListItem) {
            if (item is TodoListItem.Item) {
                binding.tvTodoTitle.text = item.title
                binding.tvTodoContent.text = item.content
                binding.switchTodo.isChecked = item.isBookmark

                binding.switchTodo.setOnClickListener {
                    onClickItem.invoke(bindingAdapterPosition, item)
                }

                binding.itemLayout.setOnClickListener {
                    onBookmarkChecked.invoke(bindingAdapterPosition, item)
                }
            }

        }

    }

    class TodoUnknownViewHolder(
        private val binding: ItemUnknownBinding,
    ) : TodoViewHolder(binding.root) {
        override fun onBind(item: TodoListItem) = Unit
    }


}