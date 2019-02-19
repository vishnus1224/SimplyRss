package com.vishnus1224.simplyrss.feedlibrary.repository.db

import arrow.core.Either
import com.vishnus1224.simplyrss.feedlibrary.Feed

internal data class DbFeed(
    val id: Long,
    val title: String,
    val url: String,
    val description: String,
    val creationDate: Long
)

internal interface FeedDatabase {
    /**
     * Save the feed in the base and return the saved feed or error.
     */
    fun saveFeed(feed: Feed): Either<FeedDatabaseException, DbFeed>

    /**
     * Retrieve all feeds or empty list.
     */
    fun getAllFeeds(): List<DbFeed>

    /**
     * Delete the feed and return nothing if success or error when deletion fails.
     */
    fun deleteFeed(feed: Feed): Either<FeedDatabaseException, Unit>
}