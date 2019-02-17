package com.vishnus1224.simplyrss.showarticles

import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import com.vishnus1224.simplyrss.showarticles.mapper.toDomain
import com.vishnus1224.simplyrss.showarticles.webservice.FetchArticleWebService

typealias GetArticlesUseCase<F> = (feedUrl: String) -> Kind<F, List<Article>>

/**
 * This use case is polymorphic with regards to F.
 * F is the container type wrapping the result of executing this use case(i.e. function).
 * F needs to be specified at the call site when instantiating this use case.
 * When F is Observable, the result will be -> Observable<List<Article>>
 * When F is Single, the result will be -> Single<List<Article>>
 * When F is Deferred from kotlin coroutines, the result will be -> Deferred<List<Article>>
 */
internal fun <F> getAllArticles(
    monadDefer: MonadDefer<F>,
    fetchArticles: FetchArticleWebService<F>
): GetArticlesUseCase<F> = { feedUrl ->
    monadDefer.run {
        this.defer {
            fetchArticles(feedUrl).map { networkArticles -> networkArticles.map { it.toDomain() } }
        }
    }
}
