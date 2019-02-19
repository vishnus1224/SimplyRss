package com.vishnus1224.simplyrss.feedlibrary.mapper

import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.feedlibrary.repository.db.DbFeed

internal fun DbFeed.toDomain() = Feed(
    id = id, title = title, feedUrl = url, description = description
)