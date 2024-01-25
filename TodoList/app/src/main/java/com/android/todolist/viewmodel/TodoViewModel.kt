package com.android.todolist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.todolist.data.TodoModel
import com.android.todolist.data.TodoUiState

class TodoViewModel : ViewModel() {
    private val _uiState: MutableLiveData<TodoUiState> = MutableLiveData(TodoUiState.init())
    val uiState: LiveData<TodoUiState> get() = _uiState

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

    fun updateTodoItem(todoModel: TodoModel?) {
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

    fun deleteTodoItem(todoModel: TodoModel?) {
        if (todoModel == null) {
            return
        }

        val updatedList = uiState.value?.list?.filter { it.id != todoModel.id }
        _uiState.value = uiState.value?.copy(list = updatedList.orEmpty())
    }

}