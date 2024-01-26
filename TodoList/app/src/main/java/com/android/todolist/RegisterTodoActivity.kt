package com.android.todolist

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
import com.android.todolist.databinding.ActivityRegisterTodoBinding

class RegisterTodoActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TODO_MODEL = "extra_todo_model"
        const val EXTRA_ENTRY_TYPE = "extra_entry_type"
        const val EXTRA_USER_ENTITY = "extra_user_entity"

        fun newIntentForCreate(
            context: Context
        ) = Intent(context, RegisterTodoActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, TodoContentType.CREATE.ordinal)
        }

        fun newIntentForUpdate(
            context: Context,
            todoModel: TodoModel? = null
        ) = Intent(context, RegisterTodoActivity::class.java).apply {
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

    private val binding: ActivityRegisterTodoBinding by lazy {
        ActivityRegisterTodoBinding.inflate(layoutInflater)
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
        contentUiState.observe(this@RegisterTodoActivity) {
            binding.etTodoTitle.setText(it.title)
            binding.etTodoContent.setText(it.content)
        }

        errorUiState.observe(this@RegisterTodoActivity) {
            it?.let {
                with(binding) {
                    tvTodoTitle.setText(it.title.message)
                    tvTodoContent.setText(it.content.message)
                }
            }
        }

        buttonUiState.observe(this@RegisterTodoActivity) { buttonState ->
            with(binding.btnRegisterTodo) {
                setText(buttonState.buttonUiState.text)
                isEnabled = buttonState.buttonUiState.enabled
            }

            /**
             * btnDeleteItem.visibility viewModel로 옮겨서 처리
             */
            binding.btnDeleteItem.visibility = buttonState.deleteButtonVisibility
        }

        intentLiveData.observe(this@RegisterTodoActivity) { intent ->
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

        btnRegisterTodo.setOnClickListener { // 등록/수정 버튼 클릭 했을 때
            val title = etTodoTitle.text.toString()
            val content = etTodoContent.text.toString()
            /**
             * viewmodel로 옮겨서 처리
             */
            viewModel.createTodoModelIntent(title, content)
        }

        btnDeleteItem.setOnClickListener { // 삭제 버튼 클릭 했을 때
            /**
             * viewmodel로 옮겨서 처리
             */
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
