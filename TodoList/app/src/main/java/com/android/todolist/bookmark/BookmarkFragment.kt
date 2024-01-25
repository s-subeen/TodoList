package com.android.todolist.bookmark

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
import com.android.todolist.databinding.FragmentBookmarkBinding
import com.android.todolist.CreateTodoActivity
import com.android.todolist.viewmodel.TodoViewModel

class BookmarkFragment : Fragment() {
    companion object {
        fun newInstance() = BookmarkFragment()
    }

    private var _binding: FragmentBookmarkBinding? = null
    private val binding: FragmentBookmarkBinding get() = _binding!!

    private val viewModel: TodoViewModel by activityViewModels()

    private val bookmarkListAdapter by lazy {
        BookmarkListAdapter()
    }

    private val updateTodoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val todoModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(
                        CreateTodoActivity.EXTRA_TODO_MODEL,
                        TodoModel::class.java
                    )
                } else {
                    result?.data?.getParcelableExtra(
                        CreateTodoActivity.EXTRA_TODO_MODEL
                    )
                }

                if (result.data?.getIntExtra(
                        CreateTodoActivity.EXTRA_ENTRY_TYPE,
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
    ): View? {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    private fun initView() {
        binding.recyclerViewBookmark.adapter = bookmarkListAdapter
        bookmarkListAdapter.setItemChangedListener(object : OnItemClickListener {
            override fun onClickSwitch(todoModel: TodoModel) {
                viewModel.updateBookmarkStatus(todoModel)
            }

            override fun onClickItem(todoModel: TodoModel) {
                updateTodoLauncher.launch(
                    CreateTodoActivity.newIntent(
                        context = requireContext(),
                        contentType = TodoContentType.UPDATE,
                        todoModel = todoModel
                    )
                )
            }
        })
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(viewLifecycleOwner) { state ->
            bookmarkListAdapter.submitList(state.list.filter { it.isBookmarked })
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}