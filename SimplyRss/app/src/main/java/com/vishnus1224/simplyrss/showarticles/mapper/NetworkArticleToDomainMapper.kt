package com.vishnus1224.simplyrss.showarticles.mapper

import com.vishnus1224.simplyrss.showarticles.Article
import com.vishnus1224.simplyrss.showarticles.webservice.NetworkArticle

internal fun NetworkArticle.toDomain(): Article = Article(
    title = title, imageUrl = imageUrl, detailsUrl = detailsUrl, description = description
)