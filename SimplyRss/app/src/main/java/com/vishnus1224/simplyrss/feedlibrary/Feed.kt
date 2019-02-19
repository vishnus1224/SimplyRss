package com.vishnus1224.simplyrss.feedlibrary

import java.lang.Math.random

data class Feed(
    val id: Long,
    val title: String,
    val feedUrl: String,
    val description: String
)

internal fun feedFrom(title: String, url: String, description: String) = Feed(
    id = 1, title = title, feedUrl = url, description = description
)