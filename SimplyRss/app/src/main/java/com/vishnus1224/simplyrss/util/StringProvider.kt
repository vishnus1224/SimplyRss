package com.vishnus1224.simplyrss.util

import android.content.Context

typealias StringProvider = (Int) -> String

fun provideAndroidStrings(context: Context): StringProvider = { resourceId -> context.getString(resourceId) }