package com.vishnus1224.simplyrss.feedlibrary.viewsavedfeeds

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import arrow.effects.ForSingleK
import arrow.effects.value
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.feedlibrary.Feed
import com.vishnus1224.simplyrss.feedlibrary.usecase.DeleteFeedUseCase
import com.vishnus1224.simplyrss.feedlibrary.usecase.GetAllFeedsUseCase
import com.vishnus1224.simplyrss.util.StringProvider
import com.vishnus1224.simplyrss.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

internal class ViewSavedFeedsViewModel(
    private val getSavedFeedsUseCase: GetAllFeedsUseCase<ForSingleK>,
    private val deleteFeedUseCase: DeleteFeedUseCase<ForSingleK>,
    private val provideString: StringProvider
) : ViewModel() {

    internal sealed class ViewState {
        data class ShowFeeds(val feeds: List<Feed>) : ViewState()
        data class ShowError(val message: String) : ViewState()
        data class ShowDeleteFeedConfirmation(val feedToDelete: Feed) : ViewState()
        data class ShowProgress(val message: String) : ViewState()
        object HideProgress : ViewState()
    }

    private val savedFeedsLiveData = MutableLiveData<ViewState>()

    private val disposables = CompositeDisposable()

    fun bindToViewState(): LiveData<ViewState> = savedFeedsLiveData

    fun getSavedFeeds() {
        getSavedFeedsUseCase
            .getAllFeeds()
            .value()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { setViewState(ViewState.HideProgress) }
            .doOnSuccess { setViewState(ViewState.HideProgress) }
            .doOnSubscribe { setViewState(ViewState.ShowProgress(provideString(R.string.loading_indicator_message))) }
            .subscribe(this::onGetSavedFeedsSuccess, this::onGetSavedFeedsFailed)
            .addTo(disposables)
    }

    fun onDeleteFeedClicked(feedToDelete: Feed) {
        setViewState(ViewState.ShowDeleteFeedConfirmation(feedToDelete))
    }

    fun onDeleteFeedConfirmed(feedToDelete: Feed) {
        deleteFeedUseCase
            .deleteFeed(feedToDelete)
            .value()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { setViewState(ViewState.HideProgress) }
            .doOnSuccess { setViewState(ViewState.HideProgress) }
            .doOnSubscribe { setViewState(ViewState.ShowProgress(provideString(R.string.progress_message_delete_feed))) }
            .subscribe(this::onDeleteFeedSuccess, this::onDeleteFeedFailed)
            .addTo(disposables)
    }

    private fun onDeleteFeedSuccess(unit: Unit) {
        getSavedFeeds()
    }

    private fun onDeleteFeedFailed(throwable: Throwable) {
        // This is a programming error and should be logged to error reporting tool.
        setViewState(ViewState.ShowError(provideString(R.string.error_message_delete_feed_failed)))
    }

    private fun onGetSavedFeedsSuccess(feeds: List<Feed>) {
        if (feeds.isNotEmpty()) setViewState(ViewState.ShowFeeds(feeds))
        else setViewState(ViewState.ShowError(provideString(R.string.error_message_empty_feeds)))
    }

    private fun onGetSavedFeedsFailed(throwable: Throwable) {
        // This is an error in implementation. Should log it to error reporting tool.
        setViewState(ViewState.ShowError(provideString(R.string.error_message_get_saved_feeds)))
    }

    private fun setViewState(viewState: ViewState) {
        savedFeedsLiveData.value = viewState
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}