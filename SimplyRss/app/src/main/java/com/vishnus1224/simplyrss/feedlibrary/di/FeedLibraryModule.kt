package com.vishnus1224.simplyrss.feedlibrary.di

import com.vishnus1224.simplyrss.di.appComponent
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepository
import com.vishnus1224.simplyrss.feedlibrary.repository.FeedRepositoryImpl
import com.vishnus1224.simplyrss.feedlibrary.repository.db.SqliteFeedDatabase
import com.vishnus1224.simplyrss.feedlibrary.repository.db.SqliteOpenHelperImpl
import java.lang.IllegalStateException

internal val feedLibraryComponent: FeedLibraryComponent
    get() = FeedLibraryModule.getComponent()

internal interface FeedLibraryComponent {
    val feedRepository: FeedRepository
}

internal object FeedLibraryComponentImpl : FeedLibraryComponent {
    override val feedRepository: FeedRepository by lazy {
        FeedRepositoryImpl(SqliteFeedDatabase(SqliteOpenHelperImpl(appComponent.application)))
    }
}

/**
 * This module provides all dependencies that are common to the feed library.
 * Modules can depend on this to get the dependencies they desire.
 */
internal class FeedLibraryModule {
    companion object {
        @JvmStatic
        private var component: FeedLibraryComponent? = null

        @JvmStatic
        fun init(component: FeedLibraryComponent) {
            this.component = component
        }

        internal fun getComponent(): FeedLibraryComponent {
            if (component != null) {
                return component!!
            } else {
                throw IllegalStateException("Feed library module not initialized, call init() before use")
            }
        }
    }
}