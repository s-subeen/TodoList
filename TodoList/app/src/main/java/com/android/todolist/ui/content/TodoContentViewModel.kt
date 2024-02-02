package com.android.todolist.ui.content

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.todolist.R
import com.android.todolist.data.TodoContentType
import com.android.todolist.data.TodoContentMessage
import com.android.todolist.data.ContentButtonUiState
import com.android.todolist.data.TodoButtonUiState
import com.android.todolist.data.TodoContentUiState
import com.android.todolist.data.TodoErrorUiState
import com.android.todolist.data.TodoIntent
import com.android.todolist.data.TodoModel
import com.android.todolist.main.Constants.Companion.EXTRA_ENTRY_TYPE
import com.android.todolist.main.Constants.Companion.EXTRA_TODO_MODEL
import java.util.UUID


class TodoContentViewModel(
    private val contentType: TodoContentType,
    private val todoModel: TodoModel?
) : ViewModel() {

    private val _contentUiState: MutableLiveData<TodoContentUiState> = MutableLiveData()
    val contentUiState: LiveData<TodoContentUiState> get() = _contentUiState

    private val _errorUiState: MutableLiveData<TodoErrorUiState> =
        MutableLiveData(TodoErrorUiState.init())
    val errorUiState: LiveData<TodoErrorUiState> get() = _errorUiState

    private val _buttonUiState: MutableLiveData<TodoButtonUiState> =
        MutableLiveData(TodoButtonUiState.init())
    val buttonUiState: LiveData<TodoButtonUiState> get() = _buttonUiState


    private val _intentLiveData: MutableLiveData<TodoIntent> = MutableLiveData()
    val intentLiveData: LiveData<TodoIntent> get() = _intentLiveData

    init {
        initContentUiState()
        updateButtonText()
    }

    private fun initContentUiState() {
        if (contentType == TodoContentType.CREATE) {
            _buttonUiState.value = buttonUiState.value?.copy(deleteButtonVisibility = View.GONE)
        } else {
            _buttonUiState.value = buttonUiState.value?.copy(deleteButtonVisibility = View.VISIBLE)
            _contentUiState.value = TodoContentUiState(
                title = todoModel?.title,
                content = todoModel?.content
            )
        }

    }

    private fun updateButtonText() {
        _buttonUiState.value = _buttonUiState.value?.copy(
            buttonUiState = _buttonUiState.value?.buttonUiState?.copy(
                text = when (contentType) {
                    TodoContentType.CREATE -> R.string.button_create
                    else -> R.string.button_update
                }
            ) ?: ContentButtonUiState.init()
        )
    }

    fun createTodoModelIntent(title: String, content: String) {
        val todoModelBuilder = TodoModel(
            id = todoModel?.id ?: UUID.randomUUID().toString(),
            title = title,
            content = content,
            isBookmarked = todoModel?.isBookmarked ?: false
        )

        val entryType =
            if (contentType == TodoContentType.CREATE) TodoContentType.CREATE.ordinal
            else TodoContentType.UPDATE.ordinal

        val intent = Intent().apply {
            putExtra(EXTRA_TODO_MODEL, todoModelBuilder)
            putExtra(EXTRA_ENTRY_TYPE, entryType)
        }

        _intentLiveData.value = TodoIntent.RegularIntent(intent)
    }

    fun createDeleteIntent() {
        val intent = Intent().apply {
            putExtra(EXTRA_TODO_MODEL, todoModel)
            putExtra(EXTRA_ENTRY_TYPE, TodoContentType.DELETE.ordinal)
        }
        _intentLiveData.value = TodoIntent.DeleteIntent(intent)
    }

    fun checkValidTitle(text: String) {
        _errorUiState.value = errorUiState.value?.copy(title = getMessageValid(text, TodoContentMessage.TITLE_BLANK))
    }

    fun checkValidContent(text: String) {
        _errorUiState.value = errorUiState.value?.copy(content = getMessageValid(text, TodoContentMessage.CONTENT_BLANK))
    }

    private fun getMessageValid(text: String, blankMessage: TodoContentMessage): TodoContentMessage {
        return if (text.isBlank()) {
            blankMessage
        } else {
            TodoContentMessage.PASS
        }
    }

    fun checkConfirmButtonEnable() {
        _buttonUiState.value = buttonUiState.value?.copy(
            buttonUiState = _buttonUiState.value?.buttonUiState?.copy(
                enabled = isConfirmButtonEnable()
            ) ?: ContentButtonUiState.init()
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