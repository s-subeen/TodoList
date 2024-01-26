package com.android.todolist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.todolist.R
import com.android.todolist.data.TodoContentType
import com.android.todolist.data.TodoContentMessage
import com.android.todolist.data.TodoContentButtonUiState
import com.android.todolist.data.TodoContentUiState
import com.android.todolist.data.TodoErrorUiState
import com.android.todolist.data.TodoModel

class TodoContentViewModel(
    private val contentType: TodoContentType,
    private val todoModel: TodoModel?
) : ViewModel() {

    private val _contentUiState: MutableLiveData<TodoContentUiState> = MutableLiveData()
    val contentUiState: LiveData<TodoContentUiState> get() = _contentUiState

    private val _buttonUiState: MutableLiveData<TodoContentButtonUiState> =
        MutableLiveData(TodoContentButtonUiState.init())
    val buttonUiState: LiveData<TodoContentButtonUiState> get() = _buttonUiState

    private val _errorUiState: MutableLiveData<TodoErrorUiState> =
        MutableLiveData(TodoErrorUiState.init())
    val errorUiState: LiveData<TodoErrorUiState> get() = _errorUiState

    init {
        initContentUiState()
        updateButtonText()
    }

    private fun initContentUiState() {
        if (contentType != TodoContentType.CREATE) {
            _contentUiState.value = TodoContentUiState(
                title = todoModel?.title,
                content = todoModel?.content
            )
        }
        Log.d("TAG", "${contentType}")
    }

    private fun updateButtonText() {
        _buttonUiState.value = _buttonUiState.value?.copy(
            text = when (contentType) {
                TodoContentType.CREATE -> R.string.button_create
                else -> R.string.button_update
            }
        )
    }

    fun checkValidTitle(text: String) {
        _errorUiState.value = errorUiState.value?.copy(title = getMessageValidTitle(text))
    }

    fun checkValidContent(text: String) {
        _errorUiState.value = errorUiState.value?.copy(content = getMessageValidContent(text))
    }

    private fun getMessageValidTitle(text: String): TodoContentMessage {
        return if (text.isBlank()) {
            TodoContentMessage.TITLE_BLANK
        } else {
            TodoContentMessage.PASS
        }
    }

    private fun getMessageValidContent(text: String): TodoContentMessage {
        return if (text.isBlank()) {
            TodoContentMessage.CONTENT_BLANK
        } else {
            TodoContentMessage.PASS
        }
    }

    fun checkConfirmButtonEnable() {
        _buttonUiState.value = buttonUiState.value?.copy(
            enabled = isConfirmButtonEnable()
        )
    }

    private fun isConfirmButtonEnable() = errorUiState.value?.let { state ->
        state.title == TodoContentMessage.PASS
                && state.content == TodoContentMessage.PASS
    } ?: false


}

class TodoContentViewModelFactory(
    private val contentType: TodoContentType,
    private val todoModel: TodoModel?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoContentViewModel::class.java)) {
            return TodoContentViewModel(
                contentType,
                todoModel
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel Class.")
        }
    }
}