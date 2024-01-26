package com.android.todolist.todo

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.android.todolist.OnItemClickListener
import com.android.todolist.data.TodoContentType
import com.android.todolist.data.TodoModel
import com.android.todolist.RegisterTodoActivity
import com.android.todolist.databinding.FragmentTodoListBinding
import com.android.todolist.viewmodel.TodoListViewModel

class TodoListFragment : Fragment() {

    companion object {
        fun newInstance() = TodoListFragment()
    }

    private var _binding: FragmentTodoListBinding? = null
    private val binding: FragmentTodoListBinding get() = _binding!!

    private val viewModel: TodoListViewModel by activityViewModels()

    private val todoListAdapter by lazy {
        TodoListAdapter()
    }

    private val updateTodoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val todoModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(
                        RegisterTodoActivity.EXTRA_TODO_MODEL,
                        TodoModel::class.java
                    )
                } else {
                    result?.data?.getParcelableExtra(
                        RegisterTodoActivity.EXTRA_TODO_MODEL
                    )
                }

                if (result.data?.getIntExtra(
                        RegisterTodoActivity.EXTRA_ENTRY_TYPE,
                        0
                    ) == TodoContentType.DELETE.ordinal
                ) {
                    viewModel.deleteTodoItem(todoModel)
                } else {
                    viewModel.updateTodoItem(todoModel)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    private fun initView() {
        binding.recyclerViewTodo.adapter = todoListAdapter
        todoListAdapter.setItemChangedListener(object : OnItemClickListener {
            override fun onClickSwitch(todoModel: TodoModel) {
                viewModel.updateBookmarkStatus(todoModel)
            }

            override fun onClickItem(todoModel: TodoModel) {
                updateTodoLauncher.launch(
                    RegisterTodoActivity.newIntentForUpdate(
                        context = requireContext(),
                        todoModel = todoModel
                    )
                )
            }
        })
    }

    // 레트로핏 최신 버전 -> 코루틴

    private fun initViewModel() = with(viewModel) {
        uiState.observe(viewLifecycleOwner) { state ->
            todoListAdapter.submitList(state.list.filter { !it.isBookmarked })
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun addTodoItem(todoModel: TodoModel?) {
        // 뷰모델로 데이터를 전달
        viewModel.addTodoItem(todoModel)
    }

}

