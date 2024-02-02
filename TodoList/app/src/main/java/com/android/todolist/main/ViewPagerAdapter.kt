package com.android.todolist.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.todolist.R
import com.android.todolist.ui.bookmark.BookmarkFragment
import com.android.todolist.ui.todo.TodoListFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf(
        TodoMainTab(
            fragment = TodoListFragment.newInstance(),
            title = R.string.fragment_todo,
            icon = R.drawable.ic_calendar_today_24
        ),
        TodoMainTab(
            fragment = BookmarkFragment.newInstance(),
            title = R.string.fragment_bookmark,
            icon = R.drawable.ic_bookmark_24
        )
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position].fragment
    }

    fun getFragment(position: Int): Fragment {
        return fragments[position].fragment
    }

    fun getTitle(position: Int): Int {
        return fragments[position].title
    }

    fun getIcon(position: Int): Int {
        return fragments[position].icon
    }

    fun getTodoFragment(): Fragment? {
        return fragments.find {
            it.fragment::class.java == TodoListFragment::class.java
        }?.fragment
    }

}