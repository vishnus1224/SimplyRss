package com.vishnus1224.simplyrss.feedlibrary.di

import arrow.effects.ForSingleK
import arrow.effects.SingleK
import arrow.effects.singlek.monadDefer.monadDefer
import arrow.effects.typeclasses.MonadDefer
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository
import com.vishnus1224.simplyrss.feedlibrary.usecase.DeleteFeedUseCase
import java.lang.IllegalStateException

internal val deleteFeedComponent: DeleteFeedWithSingleK
    get() = DeleteFeedModuleWithSingleK.getComponent()

internal interface DeleteFeedComponent<F> {
    fun deleteFeedUseCase(): DeleteFeedUseCase<F>
}

internal object DeleteFeedWithSingleK : DeleteFeedComponent<ForSingleK> {
    override fun deleteFeedUseCase(): DeleteFeedUseCase<ForSingleK> {
        return deleteWithUseCaseWithSingleK(FeedLibraryModule.getComponent().feedRepository)
    }
}

internal fun deleteWithUseCaseWithSingleK(feedRepository: FeedRepository): DeleteFeedUseCase<ForSingleK> {
    return object : DeleteFeedUseCase<ForSingleK>, MonadDefer<ForSingleK> by SingleK.monadDefer() {
        override val feedRepository: FeedRepository
            get() = feedRepository
    }
}

internal class DeleteFeedModuleWithSingleK {
    companion object {
        @JvmStatic
        private var component: DeleteFeedWithSingleK? = null

        @JvmStatic
        fun init(component: DeleteFeedWithSingleK) {
            this.component = component
        }

        internal fun getComponent(): DeleteFeedWithSingleK {
            if (component != null) {
                return component!!
            } else {
                throw IllegalStateException("Delete feed module not initialized, call init() before use")
            }
        }
    }
}