package com.vishnus1224.simplyrss.showarticles.parser

import com.einmalfel.earl.EarlParser
import com.einmalfel.earl.Item
import com.vishnus1224.simplyrss.showarticles.webservice.NetworkArticle
import java.io.InputStream

internal typealias NetworkArticleParser = (InputStream) -> List<NetworkArticle>

private fun Item.toNetworkArticle() = NetworkArticle(
    title = title,
    imageUrl = imageLink,
    detailsUrl = link,
    description = description
)

internal fun parseFeed(inputStream: InputStream): List<NetworkArticle> =
    EarlParser.parseOrThrow(inputStream, 0)
    .items
    .mapNotNull { item: Item? -> item?.toNetworkArticle() }