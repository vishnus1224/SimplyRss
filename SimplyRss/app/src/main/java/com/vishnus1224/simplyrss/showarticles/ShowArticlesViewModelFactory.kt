package com.vishnus1224.simplyrss.showarticles

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import arrow.effects.ForSingleK
import com.vishnus1224.simplyrss.util.StringProvider

internal class ShowArticlesViewModelFactory(
    private val stringProvider: StringProvider,
    private val getAllArticles: GetArticlesUseCase<ForSingleK>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShowArticlesViewModel(stringProvider ,getAllArticles) as T
    }
}