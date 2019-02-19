package com.vishnus1224.simplyrss.feedlibrary.di

import arrow.effects.ForSingleK
import arrow.effects.SingleK
import arrow.effects.singlek.monadDefer.monadDefer
import arrow.effects.typeclasses.MonadDefer
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository
import com.vishnus1224.simplyrss.feedlibrary.usecase.SaveFeedUseCase
import java.lang.IllegalStateException

internal val addNewFeedComponent: AddNewFeedWithSingleK
    get() = AddNewFeedModuleWithSingleK.getComponent()

interface AddNewFeedComponent<F> {
    val saveFeedUseCase: SaveFeedUseCase<F>
}

internal object AddNewFeedWithSingleK : AddNewFeedComponent<ForSingleK> {
    override val saveFeedUseCase: SaveFeedUseCase<ForSingleK>
        get() = saveFeedUseCaseWithSingleK(FeedLibraryModule.getComponent().feedRepository)
}

internal fun saveFeedUseCaseWithSingleK(
    feedRepository: FeedRepository
): SaveFeedUseCase<ForSingleK> = object : SaveFeedUseCase<ForSingleK>, MonadDefer<ForSingleK> by SingleK.monadDefer() {
    override val feedRepository: FeedRepository
        get() = feedRepository
}

internal class AddNewFeedModuleWithSingleK {
    companion object {
        @JvmStatic
        private var component: AddNewFeedWithSingleK? = null

        @JvmStatic
        fun init(component: AddNewFeedWithSingleK) {
            this.component = component
        }

        internal fun getComponent(): AddNewFeedWithSingleK {
            if (component != null) {
                return component!!
            } else {
                throw IllegalStateException("View saved feeds module not initialized, call init() before use")
            }
        }
    }
}