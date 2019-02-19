package com.vishnus1224.simplyrss.feedlibrary.usecase

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.MonadDefer
import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository

interface SaveFeedUseCase<F> : MonadDefer<F> {
    val feedRepository: FeedRepository

    fun saveFeed(feed: Feed): Kind<F, Feed> = defer {
        val resultOfSaveOperation: Either<Throwable, Feed> = feedRepository.saveFeed(feed)
        when (resultOfSaveOperation) {
            is Either.Left -> raiseError(resultOfSaveOperation.a)
            is Either.Right -> just(resultOfSaveOperation.b)
        }
    }
}