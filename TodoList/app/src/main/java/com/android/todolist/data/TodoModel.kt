package com.android.todolist.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class TodoModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val isBookmarked: Boolean
) : Parcelable