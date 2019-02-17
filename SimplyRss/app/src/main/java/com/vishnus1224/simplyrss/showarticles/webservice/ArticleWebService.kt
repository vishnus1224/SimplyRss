package com.vishnus1224.simplyrss.showarticles.webservice

import arrow.Kind
import arrow.typeclasses.ApplicativeError
import com.vishnus1224.simplyrss.showarticles.parser.NetworkArticleParser
import java.io.InputStream
import java.lang.Exception
import java.net.URL

internal typealias FetchArticleWebService<F> = (feedUrl: String) -> Kind<F, List<NetworkArticle>>

internal data class NetworkArticle(
    val title: String?,
    val imageUrl: String?,
    val detailsUrl: String?,
    val description: String?
)

internal fun <F> fetchArticles(
    applicativeError: ApplicativeError<F, Throwable>,
    networkArticleParser: NetworkArticleParser
): FetchArticleWebService<F> {
    return { feedUrl ->
        // The value of inputStream will be calculated only after entering try block.
        val inputStream: InputStream by lazy { URL(feedUrl).openConnection().getInputStream() }
        try {
            applicativeError.just(networkArticleParser(inputStream))
        } catch (e: Exception) {
            inputStream.close()
            // Lint raises error when type parameter is removed but also suggests to remove explicit type parameter List<NetworkArticle>
            applicativeError.raiseError<List<NetworkArticle>>(e)
        } finally {
            inputStream.close()
        }
    }
}
