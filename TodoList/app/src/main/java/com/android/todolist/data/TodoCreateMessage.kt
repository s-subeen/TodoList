package com.android.todolist.data

import androidx.annotation.StringRes
import com.android.todolist.R

enum class TodoCreateMessage(
    @StringRes val message: Int
) {

    TITLE_BLANK(R.string.error_message_title),
    CONTENT_BLANK(R.string.error_message_content),
    PASS(R.string.error_message_pass),
}