package com.vishnus1224.simplyrss.feedlibrary.usecase

import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository

interface GetAllFeedsUseCase<F> : MonadDefer<F> {
    val repository: FeedRepository

    fun getAllFeeds(): Kind<F, List<Feed>> = defer { just(repository.getAllFeeds()) }
}