package com.vishnus1224.simplyrss.feedlibrary.di

import arrow.effects.ForSingleK
import arrow.effects.SingleK
import arrow.effects.singlek.monadDefer.monadDefer
import arrow.effects.typeclasses.MonadDefer
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository
import com.vishnus1224.simplyrss.feedlibrary.usecase.GetAllFeedsUseCase
import java.lang.IllegalStateException

internal val viewSavedFeedsComponent : ViewSavedFeedsWithSingleK
    get() = ViewSavedFeedsModuleWithSingleK.getComponent()

internal interface ViewSavedFeedsComponent<F> {
    fun viewSavedFeedsUseCase(): GetAllFeedsUseCase<F>
}

internal object ViewSavedFeedsWithSingleK : ViewSavedFeedsComponent<ForSingleK> {
    override fun viewSavedFeedsUseCase(): GetAllFeedsUseCase<ForSingleK> {
        return getAllFeedsWithSingleKUseCase(FeedLibraryModule.getComponent().feedRepository)
    }
}

internal fun getAllFeedsWithSingleKUseCase(feedRepository: FeedRepository): GetAllFeedsUseCase<ForSingleK> {
    return object : GetAllFeedsUseCase<ForSingleK>, MonadDefer<ForSingleK> by SingleK.monadDefer() {
        override val repository: FeedRepository
            get() = feedRepository
    }
}

internal class ViewSavedFeedsModuleWithSingleK {
    companion object {
        @JvmStatic
        private var component: ViewSavedFeedsWithSingleK? = null

        @JvmStatic
        fun init(component: ViewSavedFeedsWithSingleK) {
            this.component = component
        }

        internal fun getComponent(): ViewSavedFeedsWithSingleK {
            if (component != null) {
                return component!!
            } else {
                throw IllegalStateException("View saved feeds module not initialized, call init() before use")
            }
        }
    }
}