package com.vishnus1224.simplyrss.showarticles

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.effects.SingleK
import com.vishnus1224.simplyrss.R
import com.vishnus1224.simplyrss.makeStringProviderWith
import com.vishnus1224.simplyrss.mock
import com.vishnus1224.simplyrss.showarticles.ShowArticlesViewModel.ViewState.*
import org.junit.Test

import com.vishnus1224.simplyrss.testSchedulersRule
import com.vishnus1224.simplyrss.util.StringProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.never
import org.mockito.Mockito.anyString
import org.mockito.Mockito.*
import java.io.IOException

internal class ShowArticlesViewModelTest {

    @Rule
    @JvmField
    val liveDataRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val schedulersRule = testSchedulersRule

    val observer = Observer<ShowArticlesViewModel.ViewState> { }.mock()

    val provideString = makeStringProviderWith(strings)

    val viewModel: ShowArticlesViewModel = createViewModel(useCaseResult = articles.right())

    @Before
    fun setup() {
        viewModel.bindViewState().observeForever(observer)
    }

    @After
    fun cleanUp() {
        viewModel.bindViewState().removeObserver(observer)
    }

    @Test
    fun `when articles are fetched successfully, show them`() {
        viewModel.fetchArticles("")

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(ShowProgress)
        inOrder.verify(observer).onChanged(HideProgress)
        inOrder.verify(observer).onChanged(ShowArticles(articles))

        verify(observer, never()).onChanged(ShowError(anyString()))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `when there is an error fetching articles, show failed to load articles error message`() {
        val newViewModel = createViewModel(useCaseResult = IOException().left())

        newViewModel.bindViewState().observeForever(observer)
        newViewModel.fetchArticles("")

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(ShowProgress)
        inOrder.verify(observer).onChanged(HideProgress)
        inOrder.verify(observer).onChanged(ShowError(provideString(R.string.error_message_load_articles)))

        verify(observer, never()).onChanged(ShowArticles(ArgumentMatchers.anyList()))
        verifyNoMoreInteractions(observer)
        newViewModel.bindViewState().removeObserver(observer)
    }

    @Test
    fun `when the response succeeds but does not contain any articles, show empty articles error message`() {
        val newViewModel = createViewModel(useCaseResult = emptyList<Article>().right())

        newViewModel.bindViewState().observeForever(observer)
        newViewModel.fetchArticles("")

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(ShowProgress)
        inOrder.verify(observer).onChanged(HideProgress)
        inOrder.verify(observer).onChanged(ShowError(provideString(R.string.error_message_empty_article_list)))

        verify(observer, never()).onChanged(ShowArticles(ArgumentMatchers.anyList()))
        verifyNoMoreInteractions(observer)
        newViewModel.bindViewState().removeObserver(observer)
    }
}

private fun createViewModel(
    stringProvider: StringProvider = makeStringProviderWith(strings),
    useCaseResult: Either<Throwable, List<Article>>
): ShowArticlesViewModel =
    ShowArticlesViewModel(
        provideString = stringProvider,
        getAllArticles = {
            when (useCaseResult) {
                is Either.Left -> SingleK.raiseError(useCaseResult.a)
                is Either.Right -> SingleK.just(useCaseResult.b)
            }
        }
    )

private val strings: Map<Int, String> = mapOf(
    R.string.error_message_empty_article_list to "empty article list",
    R.string.error_message_load_articles to "failed to load articles"
)

private val articles: List<Article> = listOf(
    Article("title 1", "image 1", "url 1", "desc 1"),
    Article("title 2", "image 2", "url 2", "desc 2")
)