package com.android.todolist.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.todolist.TodoDiffUtil
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.ItemTodoListBinding

// TODO TodoModel -> 실드 클래스로 감싸서 TodoItem
class TodoListAdapter : ListAdapter<TodoModel, TodoListAdapter.ViewHolder>(TodoDiffUtil) {

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
        // TODO 뷰가 여러 개가 나올 수 있는 형태 getViewType()
        fun bind(item: TodoModel) = with(binding){
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