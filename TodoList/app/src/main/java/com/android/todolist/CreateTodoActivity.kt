package com.android.todolist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.android.todolist.data.TodoContentType
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.ActivityCreateTodoBinding
import com.android.todolist.viewmodel.TodoContentViewModel
import com.android.todolist.viewmodel.TodoContentViewModelFactory
import java.util.UUID

class CreateTodoActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TODO_MODEL = "extra_todo_model"
        const val EXTRA_ENTRY_TYPE = "extra_entry_type"
        const val EXTRA_USER_ENTITY = "extra_user_entity"

        fun newIntent(
            context: Context,
            contentType: TodoContentType,
            todoModel: TodoModel? = null
        ) = Intent(context, CreateTodoActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, contentType.ordinal)
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

    private val binding: ActivityCreateTodoBinding by lazy {
        ActivityCreateTodoBinding.inflate(layoutInflater)
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
        contentUiState.observe(this@CreateTodoActivity) {
            binding.etTodoTitle.setText(it.title)
            binding.etTodoContent.setText(it.content)
        }

        errorUiState.observe(this@CreateTodoActivity) {
            it?.let {
                with(binding) {
                    tvTodoTitle.setText(it.title.message)
                    tvTodoContent.setText(it.content.message)
                }
            }
        }

        buttonUiState.observe(this@CreateTodoActivity) {
            with(binding.btnRegisterTodo) {
                setText(it.text)
                isEnabled = it.enabled
            }
        }
    }

    private fun initView() = with(binding) {
        setTextChangeListener()

        ivGoingBackwards.setOnClickListener {
            finish()
        }

        btnRegisterTodo.setOnClickListener {
            val intent = Intent().apply {
                val title = etTodoTitle.text.toString()
                val content = etTodoContent.text.toString()

                putExtra(
                    EXTRA_TODO_MODEL,
                    TodoModel(
                        id = todoModel?.id ?: UUID.randomUUID().toString(),
                        title = title,
                        content = content,
                        isBookmarked = todoModel?.isBookmarked ?: false
                    )
                )
                putExtra(EXTRA_ENTRY_TYPE, TodoContentType.UPDATE.ordinal)
            }

            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        btnDeleteItem.visibility =
            if (contentType == TodoContentType.UPDATE) View.VISIBLE else View.GONE
        btnDeleteItem.setOnClickListener {
            val intent = Intent().apply {
                putExtra(EXTRA_TODO_MODEL, todoModel)
                putExtra(EXTRA_ENTRY_TYPE, TodoContentType.DELETE.ordinal)
            }

            setResult(Activity.RESULT_OK, intent)
            finish()
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
