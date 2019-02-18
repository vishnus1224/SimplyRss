package com.vishnus1224.simplyrss.feedlibrary.addnewfeed

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import arrow.effects.ForSingleK
import arrow.effects.value
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.feedlibrary.addnewfeed.AddNewFeedViewModel.AddNewFeedViewState.*
import com.vishnus1224.simplyrss.feedlibrary.feedFrom
import com.vishnus1224.simplyrss.feedlibrary.usecase.SaveFeedUseCase
import com.vishnus1224.simplyrss.util.StringProvider
import com.vishnus1224.simplyrss.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

internal class AddNewFeedViewModel(
    private val saveFeedUseCase: SaveFeedUseCase<ForSingleK>,
    private val provideString: StringProvider
) : ViewModel() {

    private val viewStateLiveData = MutableLiveData<AddNewFeedViewState>()

    private val disposables = CompositeDisposable()

    internal sealed class AddNewFeedViewState {
        data class AddNewFeedSuccess(val feed: Feed) : AddNewFeedViewState()
        data class AddNewFeedFailed(val message: String) : AddNewFeedViewState()
        data class ShowErrorOnTitleField(val message: String) : AddNewFeedViewState()
        data class ShowErrorOnUrlField(val message: String) : AddNewFeedViewState()
        data class ShowErrorOnDescriptionField(val message: String) : AddNewFeedViewState()
        object ShowProgress : AddNewFeedViewState()
        object HideProgress : AddNewFeedViewState()
    }

    fun bindToViewState(): LiveData<AddNewFeedViewState> = viewStateLiveData

    fun onAddNewFeedClick(title: String, url: String, description: String) {
        val validationResult = validateAddFeedInput(title, url, description)
        when (validationResult) {
            TitleInvalid -> setViewState(ShowErrorOnTitleField(provideString(R.string.error_message_invalid_title)))
            UrlInvalid -> setViewState(ShowErrorOnUrlField(provideString(R.string.error_message_invalid_url)))
            DescriptionInvalid -> setViewState(ShowErrorOnDescriptionField(provideString(R.string.error_message_invalid_description)))
            InputIsValid -> saveFeed(feedFrom(title, url, description))
        }
    }

    private fun saveFeed(feed: Feed) {
        saveFeedUseCase
            .saveFeed(feed)
            .value()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { setViewState(AddNewFeedViewState.HideProgress) }
            .doOnSuccess { setViewState(AddNewFeedViewState.HideProgress) }
            .doOnSubscribe { setViewState(AddNewFeedViewState.ShowProgress) }
            .subscribe(this::onSaveFeedSuccess, this::onSaveFeedFailed)
            .addTo(disposables)
    }

    private fun onSaveFeedSuccess(feed: Feed) {
        setViewState(AddNewFeedViewState.AddNewFeedSuccess(feed))
    }

    private fun onSaveFeedFailed(throwable: Throwable) {
        // Decide error message based on the type of throwable.
        setViewState(AddNewFeedViewState.AddNewFeedFailed(provideString(R.string.error_message_add_feed_failed)))
    }

    private fun setViewState(viewState: AddNewFeedViewState) {
        viewStateLiveData.value = viewState
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}