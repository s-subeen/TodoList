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
import com.android.todolist.data.TodoContentType
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.FragmentBookmarkBinding
import com.android.todolist.RegisterTodoActivity
import com.android.todolist.todo.TodoListViewModel

class BookmarkFragment : Fragment() {
    companion object {
        fun newInstance() = BookmarkFragment()
    }

    private var _binding: FragmentBookmarkBinding? = null
    private val binding: FragmentBookmarkBinding get() = _binding!!

    private val viewModel: TodoListViewModel by activityViewModels()

    private val bookmarkListAdapter by lazy {
        BookmarkListAdapter()
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

                /**
                 * viewModel에서 entryType을 구분지으면 됨
                 */
                val entryType = result.data?.getIntExtra(
                    RegisterTodoActivity.EXTRA_ENTRY_TYPE,
                    0
                ) ?: TodoContentType.UPDATE.ordinal

                viewModel.handleTodoItem(entryType, todoModel)
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
        bookmarkListAdapter.setItemChangedListener(
            switchClickListener = { todoModel ->
                viewModel.updateBookmarkStatus(todoModel)
            },
            itemClickListener = { todoModel ->
                updateTodoLauncher.launch(
                    RegisterTodoActivity.newIntentForUpdate(
                        context = requireContext(),
                        todoModel = todoModel
                    )
                )
            }
        )
    }

    private fun initViewModel() = with(viewModel) {
        /**
         * filter는 viewModel에서 처리
         */
        uiState.observe(viewLifecycleOwner) {
            viewModel.filterBookmarkedItems()
        }

        viewModel.filteredTodoList.observe(viewLifecycleOwner) { filteredList ->
            bookmarkListAdapter.submitList(filteredList)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}