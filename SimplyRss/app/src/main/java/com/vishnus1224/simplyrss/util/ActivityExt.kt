package com.vishnus1224.simplyrss.util

import android.app.Activity
import java.lang.IllegalStateException

fun Activity.getStringExtraOrThrow(key: String): String {
    if (intent.hasExtra(key).not()) throw IllegalStateException("Intent does not contain an extra with key : $key")
    return intent.getStringExtra(key)
}