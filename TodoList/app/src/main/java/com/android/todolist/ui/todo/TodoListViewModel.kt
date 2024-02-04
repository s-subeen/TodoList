package com.android.todolist.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.todolist.data.TodoModel
import com.android.todolist.ui.content.TodoContentEntryType

class TodoListViewModel : ViewModel() {

    private val _event: MutableLiveData<TodoListEvent> = MutableLiveData()
    val event: LiveData<TodoListEvent> get() = _event

    private val _uiState: MutableLiveData<TodoUiState> = MutableLiveData(TodoUiState.init())
    val uiState: LiveData<TodoUiState> get() = _uiState


    fun addTodoItem(todoModel: TodoModel?) {
        if (todoModel == null) {
            return
        }
        _uiState.value = uiState.value?.copy(
            list = uiState.value?.list.orEmpty().toMutableList().apply {
                add(createTodoItem(todoModel))
            }
        )
    }

    private fun createTodoItem(model: TodoModel): TodoListItem = TodoListItem.Item(
        id = model.id,
        title = model.title,
        content = model.content
    )

    fun onClickItem(position: Int, item: TodoListItem) {
        _event.value = TodoListEvent.OpenContent(
            position,
            when (item) {
                is TodoListItem.Item -> TodoModel(
                    id = item.id,
                    title = item.title,
                    content = item.content
                )
            }
        )
    }

    fun updateTodoItem(
        entryType: TodoContentEntryType?,
        entity: TodoModel?
    ) {
        if (entity == null) {
            return
        }

        val mutableList = uiState.value?.list.orEmpty().toMutableList()

        when (entryType) {
            TodoContentEntryType.UPDATE -> {
                val position = mutableList.indexOfFirst {
                    when (it) {
                        is TodoListItem.Item -> {
                            it.id == entity.id
                        }
                    }
                }

                uiState.value?.copy(
                    list = mutableList.also { list ->
                        list[position] = createTodoItem(
                            entity
                        )
                    }
                )
            }

            TodoContentEntryType.DELETE -> {
                uiState.value?.copy(
                    list = mutableList.apply {
                        removeIf {
                            when (it) {
                                is TodoListItem.Item -> {
                                    it.id == entity.id
                                }
                            }
                        }
                    }
                )
            }

            else -> null
        }?.also { state ->
            _uiState.value = state
        }
    }


}