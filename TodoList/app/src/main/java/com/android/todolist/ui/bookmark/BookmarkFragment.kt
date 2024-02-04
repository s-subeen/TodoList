package com.android.todolist.ui.bookmark

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.FragmentBookmarkBinding
import com.android.todolist.ui.content.Constants.Companion.EXTRA_ENTRY_TYPE
import com.android.todolist.ui.content.Constants.Companion.EXTRA_TODO_MODEL
import com.android.todolist.ui.todo.TodoListViewModel

class BookmarkFragment : Fragment() {
    companion object {
        fun newInstance() = BookmarkFragment()
    }

    private var _binding: FragmentBookmarkBinding? = null
    private val binding: FragmentBookmarkBinding get() = _binding!!

    private val viewModel: TodoListViewModel by activityViewModels()

    private val bookmarkListAdapter by lazy {
        BookmarkListAdapter(
            onClickItem = { position, item ->

            },
            onBookmarkChecked = { position, item ->

            }
        )
    }

    private val updateTodoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val entity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(
                        EXTRA_TODO_MODEL,
                        TodoModel::class.java
                    )
                } else {
                    result?.data?.getParcelableExtra(
                        EXTRA_TODO_MODEL
                    )
                }

                val entryType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(
                        EXTRA_ENTRY_TYPE,
                        TodoModel::class.java
                    )
                } else {
                    result?.data?.getParcelableExtra(
                        EXTRA_ENTRY_TYPE
                    )
                }

                viewModel.updateTodoItem(
                    entity,
                    entryType
                )

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(viewLifecycleOwner) {

        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}