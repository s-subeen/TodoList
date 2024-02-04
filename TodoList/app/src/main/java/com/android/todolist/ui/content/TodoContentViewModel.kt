package com.android.todolist.ui.content

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.android.todolist.data.TodoModel
import com.android.todolist.ui.content.Constants.Companion.EXTRA_ENTRY_TYPE
import com.android.todolist.ui.content.Constants.Companion.EXTRA_USER_ENTITY
import java.util.UUID


class TodoContentViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _event: MutableLiveData<TodoContentEvent> = MutableLiveData()
    val event: LiveData<TodoContentEvent> get() = _event
    private val entryType get() = savedStateHandle.get<TodoContentEntryType>(EXTRA_ENTRY_TYPE)
    private val entity get() = savedStateHandle.get<TodoModel>(EXTRA_USER_ENTITY)

    private val _uiState: MutableLiveData<TodoContentUiState> = MutableLiveData(
        TodoContentUiState.init()
    )
    val uiState: LiveData<TodoContentUiState> get() = _uiState

    init {
        _uiState.value = uiState.value?.copy(
            title = entity?.title,
            content = entity?.content,
            button = if (entryType == TodoContentEntryType.UPDATE) {
                TodoContentButtonUiState.Create
            } else {
                TodoContentButtonUiState.Update
            }
        )

    }

    fun onClickCreate(
        title: String,
        content: String,
    ) {
        _event.value = TodoContentEvent.Create(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content
        )
    }

    fun onClickUpdate(title: String, content: String) {
        _event.value = TodoContentEvent.Update(
            id = entity?.id,
            title = title,
            content = content
        )
    }

    fun onClickDelete() {
        _event.value = TodoContentEvent.Delete(
            id = entity?.id
        )
    }

}

class TodoContentViewModelFactory {
    fun create(
        savedStateHandle: SavedStateHandle
    ) = TodoContentViewModel(
        savedStateHandle
    )
}

class TodoContentSavedStateViewModelFactory(
    private val factory: TodoContentViewModelFactory,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = factory.create(handle) as T

}