package com.android.todolist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.android.todolist.data.TodoContentType
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.ActivityRegisterTodoBinding
import com.android.todolist.viewmodel.TodoContentViewModel
import com.android.todolist.viewmodel.TodoContentViewModelFactory
import java.util.UUID
import kotlin.math.log

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

        buttonUiState.observe(this@RegisterTodoActivity) {
            with(binding.btnRegisterTodo) {
                setText(it.text)
                isEnabled = it.enabled
            }
        }
    }

    private fun initView() = with(binding) {
        setTextChangeListener()

        ivGoingBackwards.setOnClickListener { finish() }

        btnRegisterTodo.setOnClickListener { // 등록/수정 버튼 클릭 했을 때
            val title = etTodoTitle.text.toString()
            val content = etTodoContent.text.toString()

            val todoModelBuilder = TodoModel( // 데이터 설정
                id = todoModel?.id ?: UUID.randomUUID().toString(),
                title = title,
                content = content,
                isBookmarked = todoModel?.isBookmarked ?: false
            )

            val entryType = // entryType
                if (contentType == TodoContentType.CREATE) TodoContentType.CREATE.ordinal
                else TodoContentType.UPDATE.ordinal

            val intent = Intent().apply {
                putExtra(EXTRA_TODO_MODEL, todoModelBuilder)
                putExtra(EXTRA_ENTRY_TYPE, entryType)
            }

            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        btnDeleteItem.visibility =
            if (contentType == TodoContentType.UPDATE) View.VISIBLE else View.GONE

        btnDeleteItem.setOnClickListener { // 삭제 버튼 클릭 했을 때
            val deleteIntent = Intent().apply {
                putExtra(EXTRA_TODO_MODEL, todoModel)
                putExtra(EXTRA_ENTRY_TYPE, TodoContentType.DELETE.ordinal) // entryType = DELETE
            }

            setResult(Activity.RESULT_OK, deleteIntent)
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
