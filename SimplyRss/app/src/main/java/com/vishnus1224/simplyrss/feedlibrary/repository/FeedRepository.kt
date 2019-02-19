package com.vishnus1224.simplyrss.feedlibrary.repository

import arrow.core.Either
import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.feedlibrary.mapper.toDomain
import com.vishnus1224.simplyrss.feedlibrary.repository.db.FeedDatabase

interface FeedRepository {
    fun getAllFeeds(): List<Feed>
    fun saveFeed(feed: Feed): Either<Throwable, Feed>
    fun deleteFeed(feed: Feed): Either<Throwable, Unit>
}

internal class FeedRepositoryImpl(private val feedDatabase: FeedDatabase) : FeedRepository {

    override fun getAllFeeds(): List<Feed> = feedDatabase.getAllFeeds().map { it.toDomain() }

    override fun saveFeed(feed: Feed): Either<Throwable, Feed> = feedDatabase.saveFeed(feed).map { it.toDomain() }

    override fun deleteFeed(feed: Feed): Either<Throwable, Unit> = feedDatabase.deleteFeed(feed)
}