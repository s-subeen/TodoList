package com.android.todolist.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.todolist.OnItemClickListener
import com.android.todolist.TodoDiffUtil
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.ItemTodoListBinding

class BookmarkListAdapter : ListAdapter<TodoModel, BookmarkListAdapter.ViewHolder>(TodoDiffUtil) {

    private var listener: OnItemClickListener? = null

    fun setItemChangedListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvTodoTitle = binding.tvTodoTitle
        private val tvTodoContent = binding.tvTodoContent
        private val switchTodo = binding.switchTodo
        private val layout = binding.itemLayout

        fun bind(item: TodoModel) {
            tvTodoTitle.text = item.title
            tvTodoContent.text = item.content
            switchTodo.isChecked = item.isBookmarked

            switchTodo.setOnClickListener {
                listener?.onClickSwitch(item)
            }

            layout.setOnClickListener {
                listener?.onClickItem(item)
            }


        }
    }
}