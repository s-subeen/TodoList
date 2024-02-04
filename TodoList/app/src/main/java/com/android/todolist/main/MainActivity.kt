package com.android.todolist.main

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewpager2.widget.ViewPager2
import com.android.todolist.data.TodoModel
import com.android.todolist.databinding.ActivityMainBinding
import com.android.todolist.ui.content.Constants.Companion.EXTRA_TODO_MODEL
import com.android.todolist.ui.todo.TodoListFragment
import com.android.todolist.ui.content.TodoContentActivity
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewPagerAdapter: ViewPagerAdapter by lazy {
        ViewPagerAdapter(this@MainActivity)
    }

    private val contentTodoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val todoModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(
                        EXTRA_TODO_MODEL,
                        TodoModel::class.java
                    )
                } else {
                    result?.data?.getParcelableExtra(
                        EXTRA_TODO_MODEL
                    )
                }

                val fragment = viewPagerAdapter.getTodoFragment() as? TodoListFragment
                fragment?.addTodoItem(todoModel)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        onCreatedViewPager()

        onConnectTabLayout()

        onCreateTodoItem()
    }

    private fun onCreatedViewPager() = with(binding) {
        viewPager.adapter = viewPagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (viewPagerAdapter.getFragment(position) is TodoListFragment) {
                    btnAddTodoItem.show()
                } else {
                    btnAddTodoItem.hide()
                }
            }
        })
    }

    private fun onConnectTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.setText(viewPagerAdapter.getTitle(position))
            tab.setIcon(viewPagerAdapter.getIcon(position))
        }.attach()
    }

    private fun onCreateTodoItem() {
        binding.btnAddTodoItem.setOnClickListener {
            contentTodoLauncher.launch(
                TodoContentActivity.newIntentForCreate(
                    this@MainActivity
                )
            )
        }
    }

}