package com.vishnus1224.simplyrss.feedlibrary.di

import android.content.Context
import arrow.effects.ForSingleK
import arrow.effects.SingleK
import arrow.effects.singlek.monadDefer.monadDefer
import arrow.effects.typeclasses.MonadDefer
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepositoryImpl
import com.vishnus1224.simplyrss.feedlibrary.repository.db.SqliteFeedDatabase
import com.vishnus1224.simplyrss.feedlibrary.repository.db.SqliteOpenHelperImpl
import com.vishnus1224.simplyrss.feedlibrary.usecase.SaveFeedUseCase

interface AddNewFeedModule<F> {
    val saveFeedUseCase: SaveFeedUseCase<F>
    val feedRepository: FeedRepository
}

class AddNewFeedWithSingleK(
    private val applicationContext: Context
) : AddNewFeedModule<ForSingleK> {
    override val saveFeedUseCase: SaveFeedUseCase<ForSingleK>
        get() = saveFeedUseCaseWithSingleK(feedRepository)
    override val feedRepository: FeedRepository
        get() = sqliteFeedRepository(applicationContext)

}

internal fun saveFeedUseCaseWithSingleK(
    feedRepository: FeedRepository
): SaveFeedUseCase<ForSingleK> = object : SaveFeedUseCase<ForSingleK>, MonadDefer<ForSingleK> by SingleK.monadDefer() {
    override val feedRepository: FeedRepository
        get() = feedRepository
}

private val sqliteFeedRepository: (Context) -> FeedRepository = { context ->
    FeedRepositoryImpl(SqliteFeedDatabase(SqliteOpenHelperImpl(context)))
}