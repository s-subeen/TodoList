package com.android.todolist.ui.content

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.ActivityTodoContentBinding
import com.android.todolist.ui.content.Constants.Companion.EXTRA_POSITION_ENTITY
import com.android.todolist.ui.content.Constants.Companion.EXTRA_ENTRY_TYPE
import com.android.todolist.ui.content.Constants.Companion.EXTRA_USER_ENTITY

class TodoContentActivity : AppCompatActivity() {
    companion object {
        fun newIntentForCreate(
            context: Context
        ) = Intent(context, TodoContentActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, TodoContentEntryType.CREATE)
        }

        fun newIntentForUpdate(
            context: Context,
            position: Int,
            entity: TodoModel
        ) = Intent(context, TodoContentActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, TodoContentEntryType.UPDATE)
            putExtra(EXTRA_POSITION_ENTITY, position)
            putExtra(EXTRA_USER_ENTITY, entity)
        }
    }


    private val viewModel: TodoContentViewModel by viewModels {
        TodoContentSavedStateViewModelFactory(
            TodoContentViewModelFactory(),
            this,
            intent.extras
        )
    }

    private val binding: ActivityTodoContentBinding by lazy {
        ActivityTodoContentBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(this@TodoContentActivity) {
            binding.etTodoTitle.setText(it.title)
            binding.etTodoContent.setText(it.content)

            when (it.button) {
                TodoContentButtonUiState.Create -> {
                    binding.btnSubmitItem.isVisible = true
                }

                TodoContentButtonUiState.Update -> {
                    binding.btnUpdateItem.isVisible = true
                    binding.btnDeleteItem.isVisible = true
                }

                else -> Unit
            }
        }

        event.observe(this@TodoContentActivity) {
            when (it) {

                is TodoContentEvent.Create -> {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(
                            EXTRA_ENTRY_TYPE,
                            TodoContentEntryType.CREATE
                        )
                        putExtra(
                            EXTRA_USER_ENTITY,
                            TodoModel(
                                id = it.id,
                                title = it.title,
                                content = it.content
                            )
                        )
                    })
                    finish()
                }

                is TodoContentEvent.Update -> {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(
                            EXTRA_ENTRY_TYPE,
                            TodoContentEntryType.UPDATE
                        )
                        putExtra(
                            EXTRA_USER_ENTITY,
                            TodoModel(
                                id = it.id,
                                title = it.title,
                                content = it.content
                            )
                        )
                    })
                    finish()
                }

                is TodoContentEvent.Delete -> {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(
                            EXTRA_ENTRY_TYPE,
                            TodoContentEntryType.DELETE
                        )
                        putExtra(
                            EXTRA_USER_ENTITY,
                            TodoModel(
                                id = it.id,
                            )
                        )
                    })
                    finish()
                }


            }
        }

    }

    private fun initView() = with(binding) {
        ivGoingBackwards.setOnClickListener { finish() }

        btnSubmitItem.setOnClickListener {
            viewModel.onClickCreate(
                etTodoContent.text.toString(),
                etTodoContent.text.toString()
            )
        }

        btnUpdateItem.setOnClickListener {
            viewModel.onClickUpdate(
                etTodoContent.text.toString(),
                etTodoContent.text.toString()
            )
        }

        btnDeleteItem.setOnClickListener {
            viewModel.onClickDelete()
        }
    }

}
