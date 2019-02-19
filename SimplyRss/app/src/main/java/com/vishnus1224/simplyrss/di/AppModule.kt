package com.vishnus1224.simplyrss.di

import android.app.Application
import com.vishnus1224.simplyrss.feedlibrary.di.FeedLibraryComponent
import com.vishnus1224.simplyrss.feedlibrary.di.FeedLibraryComponentImpl
import com.vishnus1224.simplyrss.showarticles.di.ShowArticlesWithSingleK

internal val appComponent: AppComponent
    get() = AppModule.getComponent()

internal interface AppComponent {
    val application: Application

    fun provideFeedLibraryComponent(): FeedLibraryComponent

    fun provideShowArticlesComponent(): ShowArticlesWithSingleK
}

internal class AppComponentImpl(override val application: Application) : AppComponent {
    override fun provideShowArticlesComponent(): ShowArticlesWithSingleK {
        return ShowArticlesWithSingleK
    }

    override fun provideFeedLibraryComponent(): FeedLibraryComponent {
        return FeedLibraryComponentImpl
    }
}

internal class AppModule {
    companion object {
        @JvmStatic
        private var component: AppComponent? = null

        @JvmStatic
        fun init(component: AppComponent) {
            this.component = component
        }

        internal fun getComponent(): AppComponent {
            if (component != null) {
                return component!!
            } else {
                throw IllegalStateException("ApplicationModule not initialized, call init() before use")
            }
        }
    }
}