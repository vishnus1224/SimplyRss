package com.vishnus1224.simplyrss.di

import arrow.effects.ForSingleK
import com.vishnus1224.simplyrss.showarticles.di.ShowArticlesModule
import com.vishnus1224.simplyrss.showarticles.di.ShowArticlesWithSingleK

interface AppModule {
    fun showArticlesModule(): ShowArticlesModule<ForSingleK> = ShowArticlesWithSingleK()
}