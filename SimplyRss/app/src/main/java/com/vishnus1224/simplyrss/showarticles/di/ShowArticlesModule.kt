package com.vishnus1224.simplyrss.showarticles.di

import arrow.effects.ForSingleK
import arrow.effects.SingleK
import arrow.effects.singlek.applicativeError.applicativeError
import arrow.effects.singlek.monadDefer.monadDefer
import com.vishnus1224.simplyrss.showarticles.GetArticlesUseCase
import com.vishnus1224.simplyrss.showarticles.getAllArticles
import com.vishnus1224.simplyrss.showarticles.parser.parseFeed
import com.vishnus1224.simplyrss.showarticles.webservice.fetchArticles

interface ShowArticlesModule<F> {
    val getArticlesUseCase: GetArticlesUseCase<F>
}

class ShowArticlesWithSingleK : ShowArticlesModule<ForSingleK> {
    override val getArticlesUseCase: GetArticlesUseCase<ForSingleK> =
        getAllArticles(
            monadDefer = SingleK.monadDefer(),
            fetchArticles = fetchArticles(
                applicativeError = SingleK.applicativeError(),
                networkArticleParser = ::parseFeed
            )
        )
}

