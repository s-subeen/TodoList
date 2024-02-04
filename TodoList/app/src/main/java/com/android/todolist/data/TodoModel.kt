package com.android.todolist.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodoModel(
    val id: String?,
    val title: String? = null,
    val content: String? = null,
    val isBookmarked: Boolean = false
) : Parcelable