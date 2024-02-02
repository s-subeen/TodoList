package com.android.todolist.ui.content

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.android.todolist.data.TodoContentType
import com.android.todolist.data.TodoIntent
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.ActivityTodoContentBinding
import com.android.todolist.main.Constants.Companion.EXTRA_ENTRY_TYPE
import com.android.todolist.main.Constants.Companion.EXTRA_USER_ENTITY

class TodoContentActivity : AppCompatActivity() {
    companion object {
        fun newIntentForCreate(
            context: Context
        ) = Intent(context, TodoContentActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, TodoContentType.CREATE.ordinal)
        }

        fun newIntentForUpdate(
            context: Context,
            todoModel: TodoModel? = null
        ) = Intent(context, TodoContentActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, TodoContentType.UPDATE.ordinal)
            putExtra(EXTRA_USER_ENTITY, todoModel)
        }
    }

    private val contentType: TodoContentType by lazy {
        TodoContentType.getEntryType(
            intent?.getIntExtra(EXTRA_ENTRY_TYPE, 0)
        )
    }

    private val todoModel: TodoModel? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_USER_ENTITY, TodoModel::class.java)
        } else {
            intent?.getParcelableExtra(EXTRA_USER_ENTITY)
        }
    }

    private val viewModel: TodoContentViewModel by viewModels {
        TodoContentViewModelFactory(
            contentType = contentType,
            todoModel = todoModel
        )
    }

    private val binding: ActivityTodoContentBinding by lazy {
        ActivityTodoContentBinding.inflate(layoutInflater)
    }


    private val editTexts
        get() = with(binding) {
            listOf(
                etTodoTitle,
                etTodoContent
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(viewModel) {
        contentUiState.observe(this@TodoContentActivity) {
            binding.etTodoTitle.setText(it.title)
            binding.etTodoContent.setText(it.content)
        }

        errorUiState.observe(this@TodoContentActivity) {
            it?.let {
                with(binding) {
                    tvTodoTitle.setText(it.title.message)
                    tvTodoContent.setText(it.content.message)
                }
            }
        }

        buttonUiState.observe(this@TodoContentActivity) { buttonState ->
            with(binding.btnRegisterTodo) {
                setText(buttonState.buttonUiState.text)
                isEnabled = buttonState.buttonUiState.enabled
            }

            binding.btnDeleteItem.visibility = buttonState.deleteButtonVisibility
        }

        intentLiveData.observe(this@TodoContentActivity) { intent ->
            when (intent) {
                is TodoIntent.RegularIntent -> {
                    setResult(Activity.RESULT_OK, intent.intent)
                    finish()
                }

                is TodoIntent.DeleteIntent -> {
                    setResult(Activity.RESULT_OK, intent.intent)
                    finish()
                }
            }
        }

    }

    private fun initView() = with(binding) {
        setTextChangeListener()

        ivGoingBackwards.setOnClickListener { finish() }

        btnRegisterTodo.setOnClickListener {
            val title = etTodoTitle.text.toString()
            val content = etTodoContent.text.toString()
            viewModel.createTodoModelIntent(title, content)
        }

        btnDeleteItem.setOnClickListener {
            viewModel.createDeleteIntent()
        }
    }

    private fun setTextChangeListener() {
        editTexts.forEach { editText ->
            editText.addTextChangedListener {
                editText.setErrorMessage()
                viewModel.checkConfirmButtonEnable()
            }
        }
    }

    private fun EditText.setErrorMessage() = with(binding) {
        when (this@setErrorMessage) {
            etTodoTitle -> viewModel.checkValidTitle(etTodoTitle.text.toString())
            etTodoContent -> viewModel.checkValidContent(etTodoContent.text.toString())

            else -> Unit
        }
    }
}
