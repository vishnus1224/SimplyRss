package com.vishnus1224.simplyrss.showarticles.di

import arrow.effects.ForSingleK
import arrow.effects.SingleK
import arrow.effects.singlek.applicativeError.applicativeError
import arrow.effects.singlek.monadDefer.monadDefer
import com.vishnus1224.simplyrss.showarticles.GetArticlesUseCase
import com.vishnus1224.simplyrss.showarticles.getAllArticles
import com.vishnus1224.simplyrss.showarticles.parser.parseFeed
import com.vishnus1224.simplyrss.showarticles.webservice.fetchArticles
import java.lang.IllegalStateException

internal val showArticlesComponent: ShowArticlesWithSingleK
    get() = ShowArticlesModuleWithSingleK.getComponent()

internal interface ShowArticlesComponent<F> {
    val getArticlesUseCase: GetArticlesUseCase<F>
}

internal object ShowArticlesWithSingleK : ShowArticlesComponent<ForSingleK> {
    override val getArticlesUseCase: GetArticlesUseCase<ForSingleK>
        get() = getAllArticles(
            monadDefer = SingleK.monadDefer(),
            fetchArticles = fetchArticles(
                applicativeError = SingleK.applicativeError(),
                networkArticleParser = ::parseFeed
            )
        )
}

internal class ShowArticlesModuleWithSingleK  {

    companion object {
        @JvmStatic
        private var component: ShowArticlesWithSingleK? = null

        @JvmStatic
        fun init(component: ShowArticlesWithSingleK) {
            this.component = component
        }

        internal fun getComponent(): ShowArticlesWithSingleK {
            if (component != null) {
                return component!!
            } else {
                throw IllegalStateException("Show articles module not initialized, call init() before use")
            }
        }
    }
}

