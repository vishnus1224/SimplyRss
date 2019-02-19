package com.vishnus1224.simplyrss.feedlibrary.usecase

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.MonadDefer
import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository

interface DeleteFeedUseCase<F> : MonadDefer<F> {
    val feedRepository: FeedRepository

    fun deleteFeed(feed: Feed): Kind<F, Unit> = defer {
        val deleteOperationResult = feedRepository.deleteFeed(feed)
        when (deleteOperationResult) {
            is Either.Left -> raiseError(deleteOperationResult.a)
            is Either.Right -> just(deleteOperationResult.b)
        }
    }
}