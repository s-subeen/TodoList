package com.android.todolist.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.todolist.TodoDiffUtil
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.ItemTodoListBinding

class BookmarkListAdapter : ListAdapter<TodoModel, BookmarkListAdapter.ViewHolder>(TodoDiffUtil) {

    private var switchClickListener: ((TodoModel) -> Unit)? = null
    private var itemClickListener: ((TodoModel) -> Unit)? = null

    fun setItemChangedListener(
        switchClickListener: ((TodoModel) -> Unit)? = null,
        itemClickListener: ((TodoModel) -> Unit)? = null
    ) {
        this.switchClickListener = switchClickListener
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    ) :
        RecyclerView.ViewHolder(binding.root) {

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
