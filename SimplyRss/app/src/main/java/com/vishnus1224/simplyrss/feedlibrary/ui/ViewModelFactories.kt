package com.vishnus1224.simplyrss.feedlibrary.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import arrow.effects.ForSingleK
import com.vishnus1224.simplyrss.feedlibrary.addnewfeed.AddNewFeedViewModel
import com.vishnus1224.simplyrss.feedlibrary.usecase.SaveFeedUseCase
import com.vishnus1224.simplyrss.util.provideAndroidStrings

internal class AddNewFeedViewModelFactory(
    private val context: Context,
    private val useCase: SaveFeedUseCase<ForSingleK>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddNewFeedViewModel(useCase, provideAndroidStrings(context)) as T
    }
}