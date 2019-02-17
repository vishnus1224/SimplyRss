package com.vishnus1224.simplyrss.showarticles

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import arrow.effects.ForSingleK
import arrow.effects.value
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.util.StringProvider
import com.vishnus1224.simplyrss.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

internal class ShowArticlesViewModel(
    private val provideString: StringProvider,
    private val getAllArticles: GetArticlesUseCase<ForSingleK> // Executing this function will return the result wrapped in a Single.
) : ViewModel() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val liveData: MutableLiveData<ViewState> = MutableLiveData()

    internal sealed class ViewState {
        data class ShowArticles(val articles: List<Article>) : ViewState()
        data class ShowError(val message: String) : ViewState()
        object ShowProgress : ViewState()
        object HideProgress : ViewState()
    }

    fun bindViewState(): LiveData<ViewState> {
        return liveData
    }

    fun fetchArticles(feedUrl: String) {
        getAllArticles(feedUrl)
            .value()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { setViewState(ViewState.HideProgress) }
            .doOnSuccess { setViewState(ViewState.HideProgress) }
            .doOnSubscribe { setViewState(ViewState.ShowProgress) }
            .subscribe(this::onFetchArticlesSuccess, this::onFetchArticleFailure)
            .addTo(disposables)
    }

    private fun onFetchArticlesSuccess(articles: List<Article>) {
        if (articles.isEmpty()) {
            String::class.java
            setViewState(ViewState.ShowError(provideString(R.string.error_message_empty_article_list)))
        } else {
            setViewState(ViewState.ShowArticles(articles))
        }
    }

    private fun onFetchArticleFailure(throwable: Throwable) {
        // Decide error message based on type of throwable.
        setViewState(ViewState.ShowError(provideString(R.string.error_message_load_articles)))
    }

    private fun setViewState(state: ViewState) {
        liveData.value = state
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}