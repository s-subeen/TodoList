package com.android.todolist.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.todolist.data.TodoContentType
import com.android.todolist.data.TodoModel
import com.android.todolist.data.TodoUiState

class TodoListViewModel : ViewModel() {
    private val _uiState: MutableLiveData<TodoUiState> = MutableLiveData(TodoUiState.init())
    val uiState: LiveData<TodoUiState> get() = _uiState

    private val _filteredTodoList: MutableLiveData<List<TodoModel>> = MutableLiveData()
    val filteredTodoList: LiveData<List<TodoModel>> get() = _filteredTodoList

    fun filterBookmarkedItems() {
        _filteredTodoList.value = uiState.value?.list?.filter { it.isBookmarked } ?: emptyList()
    }

    fun addTodoItem(todoModel: TodoModel?) {
        if (todoModel == null) {
            return
        }
        _uiState.value = uiState.value?.copy(
            list = uiState.value?.list.orEmpty().toMutableList().apply {
                add(todoModel)
            }
        )
    }

    fun updateBookmarkStatus(todoModel: TodoModel?) {
        if (todoModel == null) {
            return
        }

        val updatedList = uiState.value?.list?.map {
            if (it.id == todoModel.id) {
                it.copy(isBookmarked = !it.isBookmarked)
            } else {
                it
            }
        }
        _uiState.value = uiState.value?.copy(list = updatedList.orEmpty())
    }

    private fun updateTodoItem(todoModel: TodoModel?) {
        if (todoModel == null) {
            return
        }

        val updatedList = uiState.value?.list?.map {
            if (it.id == todoModel.id) {
                it.copy(
                    title = todoModel.title,
                    content = todoModel.content
                )
            } else {
                it
            }
        }

        _uiState.value = uiState.value?.copy(list = updatedList.orEmpty())
    }

    private fun deleteTodoItem(todoModel: TodoModel?) {
        if (todoModel == null) {
            return
        }

        val updatedList = uiState.value?.list?.filter { it.id != todoModel.id }
        _uiState.value = uiState.value?.copy(list = updatedList.orEmpty())
    }

    fun handleTodoItem(entryType: Int, todoModel: TodoModel?) {
        when (entryType) {
            TodoContentType.DELETE.ordinal -> deleteTodoItem(todoModel)
            TodoContentType.UPDATE.ordinal -> updateTodoItem(todoModel)
            else -> throw IllegalArgumentException("Invalid entry type: $entryType")
        }
    }

}